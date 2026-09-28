package org.callapp.regularenglish.data.repository

import androidx.room.withTransaction
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.callapp.regularenglish.config.AppConfig
import org.callapp.regularenglish.data.local.AppDatabase
import org.callapp.regularenglish.data.local.entity.ContentSyncStateEntity
import org.callapp.regularenglish.data.local.entity.LessonEntity
import org.callapp.regularenglish.data.local.entity.TopicEntity
import org.callapp.regularenglish.data.local.entity.VocabularyEntity
import org.callapp.regularenglish.data.remote.SpeakHubApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ContentSyncRepository @Inject constructor(
    private val api: SpeakHubApi,
    private val db: AppDatabase,
    private val languageSettings: LanguageSettingsRepository
) {
    data class Progress(
        val isRunning: Boolean = false,
        val completed: Int = 0,
        val total: Int = 1,
        val message: String = "Preparing learning content",
        val errorMessage: String? = null
    ) {
        val fraction: Float get() = if (total == 0) 0f else completed.toFloat() / total
    }

    private val _progress = MutableStateFlow(Progress())
    val progress: StateFlow<Progress> = _progress.asStateFlow()

    suspend fun hasCompleteContent(): Boolean = withContext(Dispatchers.IO) {
        val language = languageSettings.current()
        db.syncStateDao().completeCount(AppConfig.APP_ID, language) > 0 && db.topicDao()
            .count(language) > 0 && db.lessonDao().count(language) > 0
    }

    /**
     * The app enters only after this succeeds. A COMPLETE Room snapshot is authoritative and
     * deliberately avoids a network request, so an offline launch never regresses to empty UI.
     */
    suspend fun prepareInitialContent(): Result<Unit> = withContext(Dispatchers.IO) {
        if (hasCompleteContent()) {
            _progress.value = Progress(false, 1, 1, "Lessons are ready offline")
            Result.success(Unit)
        } else {
            syncInitialContent()
        }
    }

    suspend fun syncInitialContent(): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching<Unit> {
            val language = languageSettings.current()
            _progress.value = Progress(true, 0, 1, "Connecting to the lesson library…")
            val topics = api.getTopics(language)
            require(topics.isNotEmpty()) { "API returned no topics" }
            _progress.value = Progress(true, 0, topics.size, "Downloading lesson list…")
            val topicDetails = topics.mapIndexed { index, topic ->
                api.getTopic(topic.id, language).also {
                    _progress.value =
                        Progress(true, index + 1, topics.size, "Loading ${topic.title}")
                }
            }
            val totalLessons = topicDetails.sumOf { it.lessons.size }.coerceAtLeast(1)
            var completedLessons = 0
            val lessonDetails = topicDetails.flatMap { topic ->
                topic.lessons.map { lesson ->
                    api.getLesson(topic.id, lesson.id, language).also {
                        completedLessons++
                        _progress.value = Progress(
                            true,
                            completedLessons,
                            totalLessons,
                            "Downloading lesson $completedLessons/$totalLessons"
                        )
                    }
                }
            }
            require(lessonDetails.isNotEmpty()) { "API returned no lessons" }
            _progress.value = Progress(true, totalLessons, totalLessons, "Saving lessons for offline learning…")
            db.withTransaction {
                db.vocabularyDao().deleteAll(language); db.lessonDao()
                .deleteAll(language); db.topicDao().deleteAll(language)
                db.topicDao().insertAll(topics.mapIndexed { index, t ->
                    TopicEntity(
                        t.id.toString(),
                        language,
                        AppConfig.APP_ID,
                        t.title,
                        "🎧",
                        t.translation?.takeIf { it.isNotBlank() } ?: t.title,
                        displayOrder = index
                    )
                })
                db.lessonDao().insertAll(lessonDetails.mapIndexed { index, l ->
                    LessonEntity(
                        l.id.toString(),
                        language,
                        l.topicId.toString(),
                        l.title,
                        "",
                        null,
                        l.content,
                        index
                    )
                })
                db.vocabularyDao().insertAll(lessonDetails.flatMap { lesson ->
                    lesson.phrases.mapNotNull { phrase ->
                        phrase.text?.trim()?.takeIf { it.isNotEmpty() }?.let { word ->
                            VocabularyEntity(
                                phrase.id,
                                language,
                                lesson.id.toString(),
                                word,
                                "",
                                phrase.translation.orEmpty()
                            )
                        }
                    }
                })
                db.syncStateDao().upsert(
                    ContentSyncStateEntity(
                        AppConfig.APP_ID,
                        language,
                        "COMPLETE",
                        schemaVersion = AppConfig.API_SCHEMA_VERSION,
                        syncedAt = System.currentTimeMillis()
                    )
                )
            }
            _progress.value = Progress(false, totalLessons, totalLessons, "Lessons are ready")
            Unit
        }.onFailure { error ->
            val errorMessage = error.message ?: error.javaClass.simpleName
            Log.e("ContentSync", "Initial content sync failed", error)
            _progress.value = Progress(
                isRunning = false,
                message = "Unable to prepare lessons",
                errorMessage = errorMessage
            )
            // Fetching is finished before the transaction below starts, so a failed attempt
            // cannot remove a previously downloaded Room snapshot.
            runCatching {
                db.syncStateDao().upsert(
                    ContentSyncStateEntity(
                        AppConfig.APP_ID,
                        languageSettings.current(),
                        "FAILED",
                        schemaVersion = AppConfig.API_SCHEMA_VERSION,
                        lastError = errorMessage
                    )
                )
            }.onFailure { stateError ->
                Log.e("ContentSync", "Could not record sync failure", stateError)
            }
        }
    }

    /** Fetches one topic only when it is absent locally; normal screen rendering remains Room-first. */
    suspend fun ensureTopicLessons(topicId: String): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching<Unit> {
            val language = languageSettings.current()
            if (db.lessonDao().countByTopic(topicId, language) > 0) return@runCatching
            val topic = api.getTopic(topicId.toLong(), language)
            val details = topic.lessons.map { lesson ->
                api.getLesson(topic.id, lesson.id, language) to lesson
            }
            require(details.isNotEmpty()) { "API returned no lessons for topic $topicId" }
            db.withTransaction {
                db.lessonDao().insertAll(details.mapIndexed { index, (lesson, summary) ->
                    LessonEntity(
                        lesson.id.toString(),
                        language,
                        lesson.topicId.toString(),
                        lesson.title,
                        "",
                        summary.translation,
                        lesson.content,
                        index
                    )
                })
                db.vocabularyDao().insertAll(details.flatMap { (lesson, _) ->
                    lesson.phrases.mapNotNull { phrase ->
                        phrase.text?.trim()?.takeIf { it.isNotEmpty() }?.let { word ->
                            VocabularyEntity(
                                phrase.id,
                                language,
                                lesson.id.toString(),
                                word,
                                "",
                                phrase.translation.orEmpty()
                            )
                        }
                    }
                })
            }
        }
    }
}
