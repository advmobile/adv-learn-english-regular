package org.callapp.regularenglish.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "content_sync_state", primaryKeys = ["app_id", "content_language"])
data class ContentSyncStateEntity(
    @ColumnInfo(name = "app_id") val appId: String,
    @ColumnInfo(name = "content_language") val contentLanguage: String,
    val status: String,
    @ColumnInfo(name = "content_version") val contentVersion: String? = null,
    @ColumnInfo(name = "schema_version") val schemaVersion: Int,
    @ColumnInfo(name = "synced_at") val syncedAt: Long? = null,
    @ColumnInfo(name = "last_error") val lastError: String? = null
)

@Entity(tableName = "topics", primaryKeys = ["id", "content_language"]) data class TopicEntity(val id: String, @ColumnInfo(name = "content_language") val contentLanguage: String, @ColumnInfo(name = "app_id") val appId: String, val title: String, val icon: String, val description: String?, val level: String = "Beginner", @ColumnInfo(name = "display_order") val displayOrder: Int = 0)
@Entity(tableName = "lessons", primaryKeys = ["id", "content_language"]) data class LessonEntity(val id: String, @ColumnInfo(name = "content_language") val contentLanguage: String, @ColumnInfo(name = "topic_id") val topicId: String, val title: String, val icon: String, val description: String?, @ColumnInfo(name = "content_html") val contentHtml: String? = null, @ColumnInfo(name = "display_order") val displayOrder: Int = 0)
@Entity(tableName = "vocabulary", primaryKeys = ["id", "content_language"]) data class VocabularyEntity(val id: Long, @ColumnInfo(name = "content_language") val contentLanguage: String, @ColumnInfo(name = "lesson_id") val lessonId: String, val word: String, val ipa: String, val meaning: String, @ColumnInfo(name = "example_en") val exampleEn: String? = null)
@Entity(tableName = "user_progress") data class UserProgressEntity(@PrimaryKey @ColumnInfo(name = "lesson_id") val lessonId: String, @ColumnInfo(name = "completion_percentage") val completionPercentage: Int = 0, @ColumnInfo(name = "is_completed") val isCompleted: Boolean = false, @ColumnInfo(name = "last_studied_at") val lastStudiedAt: Long = 0, @ColumnInfo(name = "stars_earned") val starsEarned: Int = 0)
@Entity(tableName = "study_day") data class StudyDayEntity(@PrimaryKey val day: String, @ColumnInfo(name = "active_minutes") val activeMinutes: Int = 0, @ColumnInfo(name = "xp_earned") val xpEarned: Int = 0)
@Entity(tableName = "phrase_progress") data class PhraseProgressEntity(@PrimaryKey @ColumnInfo(name = "phrase_id") val phraseId: Long, val grade: String, @ColumnInfo(name = "reviewed_at") val reviewedAt: Long)
@Entity(tableName = "learning_settings") data class LearningSettingsEntity(@PrimaryKey val id: Int = 1, @ColumnInfo(name = "daily_goal_minutes") val dailyGoalMinutes: Int = 10)
@Entity(tableName = "reminder_settings") data class ReminderSettingsEntity(@PrimaryKey val id: Int = 1, val enabled: Boolean = true, val hour: Int = 20, val minute: Int = 0)
@Entity(tableName = "language_settings") data class LanguageSettingsEntity(@PrimaryKey val id: Int = 1, val language: String = "vi")
