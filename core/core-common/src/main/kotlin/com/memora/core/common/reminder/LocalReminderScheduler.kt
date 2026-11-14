package com.memora.core.common.reminder

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.Duration
import javax.inject.Inject
import javax.inject.Singleton

private const val CHANNEL_ID = "memora_reminders"
private const val KEY_TITLE = "title"
private const val KEY_DESCRIPTION = "description"
private const val KEY_NOTIFICATION_ID = "notification_id"

/** On-device fallback for reminders. WorkManager restores scheduled work after reboot. */
@Singleton
class LocalReminderScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun schedule(id: String, title: String, description: String?, triggerAtMillis: Long) {
        val delay = (triggerAtMillis - System.currentTimeMillis()).coerceAtLeast(0)
        val request = OneTimeWorkRequestBuilder<LocalReminderWorker>()
            .setInitialDelay(Duration.ofMillis(delay))
            .setInputData(
                Data.Builder()
                    .putString(KEY_TITLE, title)
                    .putString(KEY_DESCRIPTION, description)
                    .putInt(KEY_NOTIFICATION_ID, id.hashCode())
                    .build()
            )
            .build()
        WorkManager.getInstance(context).enqueueUniqueWork(
            "memora_reminder_$id",
            ExistingWorkPolicy.REPLACE,
            request
        )
    }

    fun cancel(id: String) {
        WorkManager.getInstance(context).cancelUniqueWork("memora_reminder_$id")
    }
}

class LocalReminderWorker(appContext: Context, params: WorkerParameters) : Worker(appContext, params) {
    override fun doWork(): Result {
        if (ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return Result.success()
        }
        val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(
            NotificationChannel(CHANNEL_ID, "Memora reminders", NotificationManager.IMPORTANCE_HIGH)
        )
        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_popup_reminder)
            .setContentTitle(inputData.getString(KEY_TITLE) ?: "Memora reminder")
            .setContentText(inputData.getString(KEY_DESCRIPTION) ?: "You have a saved reminder.")
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
        NotificationManagerCompat.from(applicationContext).notify(inputData.getInt(KEY_NOTIFICATION_ID, 0), notification)
        return Result.success()
    }
}
