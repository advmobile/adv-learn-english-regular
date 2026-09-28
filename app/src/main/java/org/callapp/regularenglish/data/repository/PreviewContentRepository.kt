package org.callapp.regularenglish.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.callapp.regularenglish.domain.model.Lesson
import org.callapp.regularenglish.domain.model.Topic
import org.callapp.regularenglish.domain.model.Vocabulary
import org.callapp.regularenglish.domain.repository.ContentRepository

/** Offline preview data used only until the pre-packaged SQLite asset is supplied. */
class PreviewContentRepository : ContentRepository {
    private val topics = listOf(
        Topic("foundations", "Foundations", "🌱", "Những bài học tiếng Anh nền tảng"),
        Topic("daily_routines", "Daily Routines", "☀️", "Giao tiếp hằng ngày"),
        Topic("grammar", "Everyday Grammar", "📘", "Ngữ pháp thực hành hằng ngày")
    )
    private val lessons = listOf(
        Lesson("intro", "foundations", "Introduce Yourself", "Chào hỏi và tự giới thiệu", "Introduce yourself with confidence.", 100),
        Lesson("numbers-colors", "foundations", "Numbers & Colors", "Số đếm 1–20 và màu sắc căn bản", "Learn numbers and colors in English.", 60),
        Lesson("family", "foundations", "Family & Friends", "Gia đình và bạn bè", "Talk about your family and friends.", 0)
    )
    private val vocabulary = listOf(
        Vocabulary(1, "emerald green", "/ˈem.ər.əld ɡriːn/", "màu xanh ngọc lục bảo", "The leaf is emerald green."),
        Vocabulary(2, "one", "/wʌn/", "số một", "I have one book."),
        Vocabulary(3, "blue", "/bluː/", "màu xanh dương", "The sky is blue.")
    )
    override fun observeTopics(): Flow<List<Topic>> = flowOf(topics)
    override fun observeLessons(topicId: String): Flow<List<Lesson>> = flowOf(lessons.filter { it.topicId == topicId })
    override fun observeAllLessons(): Flow<List<Lesson>> = flowOf(lessons)
    override fun observeLesson(lessonId: String): Flow<Lesson?> = flowOf(lessons.firstOrNull { it.id == lessonId })
    override fun observeVocabulary(lessonId: String): Flow<List<Vocabulary>> = flowOf(if (lessonId == "numbers-colors") vocabulary else emptyList())
    override fun observeAllVocabulary(): Flow<List<Vocabulary>> = flowOf(vocabulary)
}
