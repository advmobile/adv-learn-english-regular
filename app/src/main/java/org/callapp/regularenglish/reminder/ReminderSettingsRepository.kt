package org.callapp.regularenglish.reminder

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import org.callapp.regularenglish.data.local.dao.ReminderSettingsDao
import org.callapp.regularenglish.data.local.entity.ReminderSettingsEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReminderSettingsRepository @Inject constructor(@ApplicationContext private val context: Context, private val dao: ReminderSettingsDao) {
    fun observe(): Flow<ReminderSettingsEntity?> = dao.observe()
    suspend fun ensureDefaultEnabled() = withContext(Dispatchers.IO) {
        if (dao.get() == null) save(true, 20, 0)
    }
    suspend fun save(enabled: Boolean, hour: Int, minute: Int) = withContext(Dispatchers.IO) {
        dao.upsert(ReminderSettingsEntity(enabled = enabled, hour = hour, minute = minute))
        if (enabled) LearningReminderScheduler.schedule(context, hour, minute) else LearningReminderScheduler.cancel(context)
    }
}
