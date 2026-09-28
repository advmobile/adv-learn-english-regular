package org.callapp.regularenglish.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import org.callapp.regularenglish.data.local.dao.ProgressDao
import org.callapp.regularenglish.data.local.entity.UserProgressEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LearningProgressRepository @Inject constructor(private val dao: ProgressDao) {
    fun observeAll(): Flow<List<UserProgressEntity>> = dao.observeAll()
    fun observeLesson(lessonId: String): Flow<UserProgressEntity?> = dao.observeByLesson(lessonId)
    suspend fun recordStudy(lessonId: String, completion: Int, xp: Int) = withContext(Dispatchers.IO) {
        val old = dao.get(lessonId)
        val strongest = maxOf(old?.completionPercentage ?: 0, completion)
        dao.upsert(UserProgressEntity(lessonId, strongest, strongest >= 100, System.currentTimeMillis(), (old?.starsEarned ?: 0) + xp))
    }
}
