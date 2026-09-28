package org.callapp.regularenglish.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import org.callapp.regularenglish.config.AppConfig
import org.callapp.regularenglish.data.local.AppDatabase
import org.callapp.regularenglish.data.repository.ContentRepositoryImpl
import org.callapp.regularenglish.domain.repository.ContentRepository
import javax.inject.Singleton
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import org.callapp.regularenglish.data.remote.SpeakHubApi

@Module @InstallIn(SingletonComponent::class) object DatabaseModule {
    private val migration1To2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("CREATE TABLE IF NOT EXISTS content_sync_state (app_id TEXT NOT NULL, content_language TEXT NOT NULL, status TEXT NOT NULL, content_version TEXT, schema_version INTEGER NOT NULL, synced_at INTEGER, last_error TEXT, PRIMARY KEY(app_id, content_language))")
        }
    }
    private val migration2To3 = object : Migration(2, 3) { override fun migrate(db: SupportSQLiteDatabase) { db.execSQL("CREATE TABLE IF NOT EXISTS study_day (day TEXT NOT NULL, active_minutes INTEGER NOT NULL, xp_earned INTEGER NOT NULL, PRIMARY KEY(day))"); db.execSQL("CREATE TABLE IF NOT EXISTS phrase_progress (phrase_id INTEGER NOT NULL, grade TEXT NOT NULL, reviewed_at INTEGER NOT NULL, PRIMARY KEY(phrase_id))"); db.execSQL("CREATE TABLE IF NOT EXISTS learning_settings (id INTEGER NOT NULL, daily_goal_minutes INTEGER NOT NULL, PRIMARY KEY(id))") } }
    private val migration3To4 = object : Migration(3, 4) { override fun migrate(db: SupportSQLiteDatabase) { db.execSQL("CREATE TABLE IF NOT EXISTS reminder_settings (id INTEGER NOT NULL, enabled INTEGER NOT NULL, hour INTEGER NOT NULL, minute INTEGER NOT NULL, PRIMARY KEY(id))") } }
    private val migration4To5 = object : Migration(4, 5) { override fun migrate(db: SupportSQLiteDatabase) { db.execSQL("CREATE TABLE IF NOT EXISTS language_settings (id INTEGER NOT NULL, language TEXT NOT NULL, PRIMARY KEY(id))") } }
    private val migration5To6 = object : Migration(5, 6) { override fun migrate(db: SupportSQLiteDatabase) { db.execSQL("ALTER TABLE topics RENAME TO topics_old"); db.execSQL("CREATE TABLE topics (id TEXT NOT NULL, content_language TEXT NOT NULL, app_id TEXT NOT NULL, title TEXT NOT NULL, icon TEXT NOT NULL, description TEXT, level TEXT NOT NULL, display_order INTEGER NOT NULL, PRIMARY KEY(id, content_language))"); db.execSQL("INSERT INTO topics SELECT id, 'vi', app_id, title, icon, description, level, display_order FROM topics_old"); db.execSQL("DROP TABLE topics_old"); db.execSQL("ALTER TABLE lessons RENAME TO lessons_old"); db.execSQL("CREATE TABLE lessons (id TEXT NOT NULL, content_language TEXT NOT NULL, topic_id TEXT NOT NULL, title TEXT NOT NULL, icon TEXT NOT NULL, description TEXT, content_html TEXT, display_order INTEGER NOT NULL, PRIMARY KEY(id, content_language))"); db.execSQL("INSERT INTO lessons SELECT id, 'vi', topic_id, title, icon, description, content_html, display_order FROM lessons_old"); db.execSQL("DROP TABLE lessons_old"); db.execSQL("ALTER TABLE vocabulary RENAME TO vocabulary_old"); db.execSQL("CREATE TABLE vocabulary (id INTEGER NOT NULL, content_language TEXT NOT NULL, lesson_id TEXT NOT NULL, word TEXT NOT NULL, ipa TEXT NOT NULL, meaning TEXT NOT NULL, example_en TEXT, PRIMARY KEY(id, content_language))"); db.execSQL("INSERT INTO vocabulary SELECT id, 'vi', lesson_id, word, ipa, meaning, example_en FROM vocabulary_old"); db.execSQL("DROP TABLE vocabulary_old") } }

    @Provides @Singleton fun database(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "advmobile_${AppConfig.APP_ID}.db")
            .addMigrations(migration1To2, migration2To3, migration3To4, migration4To5, migration5To6)
            .build()
    }
    @Provides fun topicDao(db: AppDatabase) = db.topicDao(); @Provides fun lessonDao(db: AppDatabase) = db.lessonDao(); @Provides fun vocabularyDao(db: AppDatabase) = db.vocabularyDao(); @Provides fun syncStateDao(db: AppDatabase) = db.syncStateDao(); @Provides fun progressDao(db: AppDatabase) = db.progressDao(); @Provides fun studyDayDao(db: AppDatabase) = db.studyDayDao(); @Provides fun phraseProgressDao(db: AppDatabase) = db.phraseProgressDao(); @Provides fun learningSettingsDao(db: AppDatabase) = db.learningSettingsDao(); @Provides fun reminderSettingsDao(db: AppDatabase) = db.reminderSettingsDao(); @Provides fun languageSettingsDao(db: AppDatabase) = db.languageSettingsDao()
    @Provides @Singleton fun contentRepository(db: AppDatabase, languageSettings: org.callapp.regularenglish.data.repository.LanguageSettingsRepository): ContentRepository = ContentRepositoryImpl(db.topicDao(), db.lessonDao(), db.vocabularyDao(), languageSettings)
    @Provides @Singleton fun speakHubApi(): SpeakHubApi = Retrofit.Builder().baseUrl(AppConfig.API_BASE_URL).client(OkHttpClient.Builder().connectTimeout(15, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS).build()).addConverterFactory(GsonConverterFactory.create()).build().create(SpeakHubApi::class.java)
}
