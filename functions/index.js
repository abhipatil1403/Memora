const {onSchedule} = require("firebase-functions/v2/scheduler");
const {logger} = require("firebase-functions");
const admin = require("firebase-admin");

admin.initializeApp();

const db = admin.firestore();
const LEASE_MS = 10 * 60 * 1000;
const MAX_REMINDERS_PER_RUN = 100;

/**
 * Delivers each due reminder once. It uses a Firestore lease before FCM delivery,
 * so overlapping scheduler invocations do not send duplicate notifications.
 *
 * Reminder contract (under users/{uid}/reminders/{id}):
 * dateTime: epoch milliseconds, isCompleted: boolean, notifiedAt: optional epoch
 * milliseconds, title: string, description: optional string.
 */
exports.sendDueReminders = onSchedule(
  {
    schedule: "every 1 minutes",
    timeZone: "UTC",
    region: "asia-south1",
    retryCount: 3,
    maxInstances: 1,
  },
  async () => {
    const now = Date.now();
    const due = await db.collectionGroup("reminders")
      .where("isCompleted", "==", false)
      .where("notifiedAt", "==", null)
      .where("dateTime", "<=", now)
      .orderBy("dateTime", "asc")
      .limit(MAX_REMINDERS_PER_RUN)
      .get();

    const results = await Promise.allSettled(due.docs.map((doc) => deliverReminder(doc.ref, now)));
    const failed = results.filter((result) => result.status === "rejected");
    failed.forEach((result) => logger.error("Reminder delivery failed", result.reason));
    logger.info("Due-reminder run finished", {due: due.size, delivered: due.size - failed.length, failed: failed.length});
  },
);

async function deliverReminder(reminderRef, now) {
  const claimed = await db.runTransaction(async (tx) => {
    const snapshot = await tx.get(reminderRef);
    if (!snapshot.exists) return false;

    const reminder = snapshot.data();
    const leaseUntil = reminder.notificationLeaseUntil || 0;
    if (reminder.isCompleted || reminder.notifiedAt != null || leaseUntil > now) return false;

    tx.update(reminderRef, {
      notificationLeaseUntil: now + LEASE_MS,
      notificationAttemptedAt: now,
    });
    return true;
  });

  if (!claimed) return;

  const userId = reminderRef.parent.parent.id;
  const reminder = (await reminderRef.get()).data();
  const tokenSnapshot = await db.collection("users").doc(userId).collection("deviceTokens").get();
  const tokens = tokenSnapshot.docs.map((doc) => doc.get("token")).filter(Boolean);

  if (tokens.length === 0) {
    await reminderRef.update({notificationLeaseUntil: admin.firestore.FieldValue.delete()});
    logger.info("No device token for due reminder", {userId, reminderId: reminderRef.id});
    return;
  }

  const response = await admin.messaging().sendEachForMulticast({
    tokens,
    notification: {
      title: reminder.title || "Memora reminder",
      body: reminder.description || "You have a saved reminder.",
    },
    data: {
      type: "reminder",
      reminderId: reminderRef.id,
      memoryId: String(reminder.memoryId || ""),
    },
    android: {
      priority: "high",
      notification: {channelId: "memora_reminders"},
    },
  });

  const tokenDeletes = [];
  response.responses.forEach((result, index) => {
    const code = result.error?.code;
    if (code === "messaging/registration-token-not-registered" || code === "messaging/invalid-registration-token") {
      tokenDeletes.push(tokenSnapshot.docs[index].ref.delete());
    }
  });
  await Promise.all(tokenDeletes);

  if (response.successCount === 0) {
    await reminderRef.update({notificationLeaseUntil: admin.firestore.FieldValue.delete()});
    throw new Error(`No FCM delivery succeeded for reminder ${reminderRef.id}`);
  }

  await reminderRef.update({
    notifiedAt: Date.now(),
    notificationLeaseUntil: admin.firestore.FieldValue.delete(),
    notificationAttemptedAt: now,
  });
}
