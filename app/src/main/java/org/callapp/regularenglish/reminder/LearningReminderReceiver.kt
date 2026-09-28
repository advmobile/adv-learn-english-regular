package org.callapp.regularenglish.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import org.callapp.regularenglish.R
import android.app.PendingIntent
import org.callapp.regularenglish.MainActivity

/** Receiver declaration for the daily learning reminder. Notification scheduling follows next. */
class LearningReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val pending = goAsync()
        Thread {
        val channelId = "daily_learning_reminder"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Daily study reminder", NotificationManager.IMPORTANCE_DEFAULT).apply { description = "A reminder to continue an unfinished lesson" }
            context.getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(context.getString(R.string.notification_title))
            .setContentText(context.getString(R.string.notification_body))
            .setAutoCancel(true)
            .setContentIntent(PendingIntent.getActivity(context, 2001, Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            }, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE))
            .build()
        NotificationManagerCompat.from(context).notify(2001, notification)
            pending.finish()
        }.start()
    }
}
