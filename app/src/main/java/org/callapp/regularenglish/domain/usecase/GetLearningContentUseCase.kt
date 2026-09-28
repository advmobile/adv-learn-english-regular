package org.callapp.regularenglish.domain.usecase

import javax.inject.Inject
import org.callapp.regularenglish.domain.repository.ContentRepository

class GetLearningContentUseCase @Inject constructor(private val repository: ContentRepository) {
    fun topics() = repository.observeTopics()
    fun lessons(topicId: String) = repository.observeLessons(topicId)
    fun vocabulary(lessonId: String) = repository.observeVocabulary(lessonId)
}
