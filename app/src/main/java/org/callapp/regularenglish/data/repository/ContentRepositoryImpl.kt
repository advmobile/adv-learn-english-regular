package org.callapp.regularenglish.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.flatMapLatest
import org.callapp.regularenglish.data.local.dao.LessonDao
import org.callapp.regularenglish.data.local.dao.TopicDao
import org.callapp.regularenglish.data.local.dao.VocabularyDao
import org.callapp.regularenglish.data.local.entity.LessonEntity
import org.callapp.regularenglish.domain.model.Lesson
import org.callapp.regularenglish.domain.model.Topic
import org.callapp.regularenglish.domain.model.Vocabulary
import org.callapp.regularenglish.domain.repository.ContentRepository
import javax.inject.Inject

class ContentRepositoryImpl @Inject constructor(private val topics: TopicDao, private val lessons: LessonDao, private val vocabulary: VocabularyDao, private val languageSettings: LanguageSettingsRepository) : ContentRepository {
    private val language = languageSettings.observe().map { it?.language ?: "vi" }
    override fun observeTopics(): Flow<List<Topic>> = language.flatMapLatest { lang -> topics.observeAll(lang) }.map { rows -> rows.map { Topic(it.id, it.title, it.icon, it.description.orEmpty()) } }
    private fun LessonEntity.toDomain() = Lesson(id, topicId, title, description.orEmpty(), contentHtml.orEmpty(), 0)
    override fun observeLessons(topicId: String): Flow<List<Lesson>> = language.flatMapLatest { lang -> lessons.observeByTopic(topicId, lang) }.map { rows -> rows.map { it.toDomain() } }
    override fun observeAllLessons(): Flow<List<Lesson>> = language.flatMapLatest { lang -> lessons.observeAll(lang) }.map { rows -> rows.map { it.toDomain() } }
    override fun observeLesson(lessonId: String): Flow<Lesson?> = language.flatMapLatest { lang -> lessons.observeById(lessonId, lang) }.map { it?.toDomain() }
    override fun observeVocabulary(lessonId: String): Flow<List<Vocabulary>> = language.flatMapLatest { lang -> vocabulary.observeByLesson(lessonId, lang) }.map { rows -> rows.map { Vocabulary(it.id, it.word, it.ipa, it.meaning, it.exampleEn.orEmpty(), it.lessonId) } }
    override fun observeAllVocabulary(): Flow<List<Vocabulary>> = language.flatMapLatest { lang -> vocabulary.observeAll(lang) }.map { rows -> rows.map { Vocabulary(it.id, it.word, it.ipa, it.meaning, it.exampleEn.orEmpty(), it.lessonId) } }
}
