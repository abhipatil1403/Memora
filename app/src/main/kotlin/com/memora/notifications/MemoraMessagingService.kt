package com.memora.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.memora.R
import timber.log.Timber

/** Receives FCM reminders sent by the trusted scheduling backend / Cloud Function. */
class MemoraMessagingService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val payload = mapOf(
            "token" to token,
            "platform" to "android",
            "updatedAt" to System.currentTimeMillis()
        )
        FirebaseFirestore.getInstance().collection("users").document(userId)
            .collection("deviceTokens").document(token)
            .set(payload)
            .addOnFailureListener { Timber.w(it, "Could not register FCM token") }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val title = message.notification?.title ?: message.data["title"] ?: "Memora reminder"
        val body = message.notification?.body ?: message.data["body"] ?: "You have a saved reminder."
        showNotification(title, body)
    }

    private fun showNotification(title: String, body: String) {
        val channelId = "memora_reminders"
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(
            NotificationChannel(channelId, "Memora reminders", NotificationManager.IMPORTANCE_HIGH)
        )
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
        NotificationManagerCompat.from(this).notify((System.currentTimeMillis() % Int.MAX_VALUE).toInt(), notification)
    }
}
