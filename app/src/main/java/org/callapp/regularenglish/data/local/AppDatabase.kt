package org.callapp.regularenglish.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import org.callapp.regularenglish.data.local.dao.LessonDao
import org.callapp.regularenglish.data.local.dao.TopicDao
import org.callapp.regularenglish.data.local.dao.VocabularyDao
import org.callapp.regularenglish.data.local.dao.ContentSyncStateDao
import org.callapp.regularenglish.data.local.dao.ProgressDao
import org.callapp.regularenglish.data.local.dao.StudyDayDao
import org.callapp.regularenglish.data.local.dao.PhraseProgressDao
import org.callapp.regularenglish.data.local.dao.LearningSettingsDao
import org.callapp.regularenglish.data.local.dao.ReminderSettingsDao
import org.callapp.regularenglish.data.local.dao.LanguageSettingsDao
import org.callapp.regularenglish.data.local.entity.ContentSyncStateEntity
import org.callapp.regularenglish.data.local.entity.LessonEntity
import org.callapp.regularenglish.data.local.entity.TopicEntity
import org.callapp.regularenglish.data.local.entity.UserProgressEntity
import org.callapp.regularenglish.data.local.entity.VocabularyEntity
import org.callapp.regularenglish.data.local.entity.StudyDayEntity
import org.callapp.regularenglish.data.local.entity.PhraseProgressEntity
import org.callapp.regularenglish.data.local.entity.LearningSettingsEntity
import org.callapp.regularenglish.data.local.entity.ReminderSettingsEntity
import org.callapp.regularenglish.data.local.entity.LanguageSettingsEntity

@Database(entities = [TopicEntity::class, LessonEntity::class, VocabularyEntity::class, UserProgressEntity::class, ContentSyncStateEntity::class, StudyDayEntity::class, PhraseProgressEntity::class, LearningSettingsEntity::class, ReminderSettingsEntity::class, LanguageSettingsEntity::class], version = 6, exportSchema = false)
abstract class AppDatabase : RoomDatabase() { abstract fun topicDao(): TopicDao; abstract fun lessonDao(): LessonDao; abstract fun vocabularyDao(): VocabularyDao; abstract fun syncStateDao(): ContentSyncStateDao; abstract fun progressDao(): ProgressDao; abstract fun studyDayDao(): StudyDayDao; abstract fun phraseProgressDao(): PhraseProgressDao; abstract fun learningSettingsDao(): LearningSettingsDao; abstract fun reminderSettingsDao(): ReminderSettingsDao; abstract fun languageSettingsDao(): LanguageSettingsDao }
