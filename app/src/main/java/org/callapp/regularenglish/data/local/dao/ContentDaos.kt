package org.callapp.regularenglish.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import org.callapp.regularenglish.data.local.entity.LessonEntity
import org.callapp.regularenglish.data.local.entity.TopicEntity
import org.callapp.regularenglish.data.local.entity.VocabularyEntity
import org.callapp.regularenglish.data.local.entity.ContentSyncStateEntity
import org.callapp.regularenglish.data.local.entity.UserProgressEntity
import org.callapp.regularenglish.data.local.entity.StudyDayEntity
import org.callapp.regularenglish.data.local.entity.PhraseProgressEntity
import org.callapp.regularenglish.data.local.entity.LearningSettingsEntity
import org.callapp.regularenglish.data.local.entity.ReminderSettingsEntity
import org.callapp.regularenglish.data.local.entity.LanguageSettingsEntity

@Dao interface TopicDao { @Query("SELECT * FROM topics WHERE content_language = :language ORDER BY display_order") fun observeAll(language: String): Flow<List<TopicEntity>>; @Query("SELECT COUNT(*) FROM topics WHERE content_language = :language") fun count(language: String): Int; @androidx.room.Insert(onConflict = androidx.room.OnConflictStrategy.REPLACE) fun insertAll(rows: List<TopicEntity>); @Query("DELETE FROM topics WHERE content_language = :language") fun deleteAll(language: String) }
@Dao interface LessonDao { @Query("SELECT * FROM lessons WHERE content_language = :language ORDER BY display_order") fun observeAll(language: String): Flow<List<LessonEntity>>; @Query("SELECT * FROM lessons WHERE topic_id = :topicId AND content_language = :language ORDER BY display_order") fun observeByTopic(topicId: String, language: String): Flow<List<LessonEntity>>; @Query("SELECT * FROM lessons WHERE id = :lessonId AND content_language = :language LIMIT 1") fun observeById(lessonId: String, language: String): Flow<LessonEntity?>; @Query("SELECT * FROM lessons WHERE id = :lessonId AND content_language = :language LIMIT 1") fun getById(lessonId: String, language: String): LessonEntity?; @Query("SELECT COUNT(*) FROM lessons WHERE content_language = :language") fun count(language: String): Int; @Query("SELECT COUNT(*) FROM lessons WHERE topic_id = :topicId AND content_language = :language") fun countByTopic(topicId: String, language: String): Int; @androidx.room.Insert(onConflict = androidx.room.OnConflictStrategy.REPLACE) fun insertAll(rows: List<LessonEntity>); @Query("DELETE FROM lessons WHERE content_language = :language") fun deleteAll(language: String) }
@Dao interface VocabularyDao { @Query("SELECT * FROM vocabulary WHERE lesson_id = :lessonId AND content_language = :language") fun observeByLesson(lessonId: String, language: String): Flow<List<VocabularyEntity>>; @Query("SELECT * FROM vocabulary WHERE content_language = :language") fun observeAll(language: String): Flow<List<VocabularyEntity>>; @androidx.room.Insert(onConflict = androidx.room.OnConflictStrategy.REPLACE) fun insertAll(rows: List<VocabularyEntity>); @Query("DELETE FROM vocabulary WHERE content_language = :language") fun deleteAll(language: String) }
@Dao interface ProgressDao { @Query("SELECT * FROM user_progress") fun observeAll(): Flow<List<UserProgressEntity>>; @Query("SELECT COUNT(*) FROM user_progress WHERE is_completed = 0") fun unfinishedCount(): Int; @Query("SELECT * FROM user_progress WHERE is_completed = 0 ORDER BY last_studied_at DESC LIMIT 1") fun getMostRecentUnfinished(): UserProgressEntity?; @Query("SELECT * FROM user_progress WHERE lesson_id = :lessonId LIMIT 1") fun observeByLesson(lessonId: String): Flow<UserProgressEntity?>; @Query("SELECT * FROM user_progress WHERE lesson_id = :lessonId LIMIT 1") fun get(lessonId: String): UserProgressEntity?; @androidx.room.Insert(onConflict = androidx.room.OnConflictStrategy.REPLACE) fun upsert(row: UserProgressEntity); }
@Dao interface StudyDayDao { @Query("SELECT * FROM study_day ORDER BY day DESC") fun observeAll(): Flow<List<StudyDayEntity>>; @Query("SELECT * FROM study_day WHERE day = :day") fun get(day: String): StudyDayEntity?; @androidx.room.Insert(onConflict = androidx.room.OnConflictStrategy.REPLACE) fun upsert(row: StudyDayEntity) }
@Dao interface PhraseProgressDao { @Query("SELECT * FROM phrase_progress") fun observeAll(): Flow<List<PhraseProgressEntity>>; @androidx.room.Insert(onConflict = androidx.room.OnConflictStrategy.REPLACE) fun upsert(row: PhraseProgressEntity) }
@Dao interface LearningSettingsDao { @Query("SELECT * FROM learning_settings WHERE id = 1") fun observe(): Flow<LearningSettingsEntity?>; @androidx.room.Insert(onConflict = androidx.room.OnConflictStrategy.REPLACE) fun upsert(row: LearningSettingsEntity) }
@Dao interface ReminderSettingsDao { @Query("SELECT * FROM reminder_settings WHERE id = 1") fun observe(): Flow<ReminderSettingsEntity?>; @Query("SELECT * FROM reminder_settings WHERE id = 1") fun get(): ReminderSettingsEntity?; @androidx.room.Insert(onConflict = androidx.room.OnConflictStrategy.REPLACE) fun upsert(row: ReminderSettingsEntity) }
@Dao interface LanguageSettingsDao { @Query("SELECT * FROM language_settings WHERE id = 1") fun observe(): Flow<LanguageSettingsEntity?>; @Query("SELECT * FROM language_settings WHERE id = 1") fun get(): LanguageSettingsEntity?; @androidx.room.Insert(onConflict = androidx.room.OnConflictStrategy.REPLACE) fun upsert(row: LanguageSettingsEntity) }

@Dao
interface ContentSyncStateDao {
    @androidx.room.Insert(onConflict = androidx.room.OnConflictStrategy.REPLACE)
    fun upsert(state: ContentSyncStateEntity): Long

    @Query("SELECT COUNT(*) FROM content_sync_state WHERE app_id = :appId AND content_language = :language AND status = 'COMPLETE'")
    fun completeCount(appId: String, language: String): Int
}
