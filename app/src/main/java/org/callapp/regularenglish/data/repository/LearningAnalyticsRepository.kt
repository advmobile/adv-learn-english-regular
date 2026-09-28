package org.callapp.regularenglish.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.withContext
import org.callapp.regularenglish.data.local.dao.LearningSettingsDao
import org.callapp.regularenglish.data.local.dao.PhraseProgressDao
import org.callapp.regularenglish.data.local.dao.StudyDayDao
import org.callapp.regularenglish.data.local.entity.LearningSettingsEntity
import org.callapp.regularenglish.data.local.entity.PhraseProgressEntity
import org.callapp.regularenglish.data.local.entity.StudyDayEntity
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

data class LearningAnalytics(val streak: Int, val totalXp: Int, val masteredWords: Int, val todayMinutes: Int, val dailyGoalMinutes: Int)

@Singleton
class LearningAnalyticsRepository @Inject constructor(private val days: StudyDayDao, private val phrases: PhraseProgressDao, private val settings: LearningSettingsDao) {
    fun observePhraseProgress(): Flow<List<PhraseProgressEntity>> = phrases.observeAll()
    fun observe(): Flow<LearningAnalytics> = combine(days.observeAll(), phrases.observeAll(), settings.observe()) { studyDays, phraseRows, setting ->
        val daySet = studyDays.map { it.day }.toSet(); var cursor = LocalDate.now(); var streak = 0
        while (daySet.contains(cursor.toString())) { streak++; cursor = cursor.minusDays(1) }
        val today = studyDays.firstOrNull { it.day == LocalDate.now().toString() }
        LearningAnalytics(streak, studyDays.sumOf { it.xpEarned }, phraseRows.count { it.grade == "MASTERED" }, today?.activeMinutes ?: 0, setting?.dailyGoalMinutes ?: 10)
    }
    suspend fun recordStudy(minutes: Int, xp: Int) = withContext(Dispatchers.IO) { val key = LocalDate.now().toString(); val old = days.get(key); days.upsert(StudyDayEntity(key, (old?.activeMinutes ?: 0) + minutes, (old?.xpEarned ?: 0) + xp)) }
    suspend fun setDailyGoal(minutes: Int) = withContext(Dispatchers.IO) { settings.upsert(LearningSettingsEntity(dailyGoalMinutes = minutes)) }
    suspend fun ratePhrase(id: Long, mastered: Boolean) = withContext(Dispatchers.IO) { phrases.upsert(PhraseProgressEntity(id, if (mastered) "MASTERED" else "REVIEW", System.currentTimeMillis())) }
}
