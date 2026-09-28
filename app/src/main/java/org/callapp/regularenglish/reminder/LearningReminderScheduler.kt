package org.callapp.regularenglish.reminder

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.util.Calendar

object LearningReminderScheduler {
    private const val REQUEST_CODE = 2001
    fun schedule(context: Context, hour: Int, minute: Int) {
        val pending = pendingIntent(context)
        val time = Calendar.getInstance().apply { set(Calendar.HOUR_OF_DAY, hour); set(Calendar.MINUTE, minute); set(Calendar.SECOND, 0); if (before(Calendar.getInstance())) add(Calendar.DAY_OF_YEAR, 1) }
        context.getSystemService(AlarmManager::class.java).setInexactRepeating(AlarmManager.RTC_WAKEUP, time.timeInMillis, AlarmManager.INTERVAL_DAY, pending)
    }
    fun cancel(context: Context) { context.getSystemService(AlarmManager::class.java).cancel(pendingIntent(context)) }
    private fun pendingIntent(context: Context) = PendingIntent.getBroadcast(context, REQUEST_CODE, Intent(context, LearningReminderReceiver::class.java), PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
}
