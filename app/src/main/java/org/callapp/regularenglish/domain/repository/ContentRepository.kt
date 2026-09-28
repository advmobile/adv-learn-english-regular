package org.callapp.regularenglish.domain.repository

import kotlinx.coroutines.flow.Flow
import org.callapp.regularenglish.domain.model.Lesson
import org.callapp.regularenglish.domain.model.Topic
import org.callapp.regularenglish.domain.model.Vocabulary

interface ContentRepository {
    fun observeTopics(): Flow<List<Topic>>
    fun observeLessons(topicId: String): Flow<List<Lesson>>
    fun observeAllLessons(): Flow<List<Lesson>>
    fun observeLesson(lessonId: String): Flow<Lesson?>
    fun observeVocabulary(lessonId: String): Flow<List<Vocabulary>>
    fun observeAllVocabulary(): Flow<List<Vocabulary>>
}
