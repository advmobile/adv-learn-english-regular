package org.callapp.regularenglish.data.remote

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface SpeakHubApi {
    @GET("topics")
    suspend fun getTopics(@Query("lang") language: String): List<TopicDto>
    @GET("topics/{topicId}")
    suspend fun getTopic(@Path("topicId") topicId: Long, @Query("lang") language: String): TopicDetailDto
    @GET("topics/{topicId}/lessons/{lessonId}")
    suspend fun getLesson(@Path("topicId") topicId: Long, @Path("lessonId") lessonId: Long, @Query("lang") language: String): LessonDetailDto
}
data class TopicDto(val id: Long, val title: String, val translation: String?)
data class TopicDetailDto(val id: Long, val title: String, val lessons: List<LessonSummaryDto>)
data class LessonSummaryDto(val id: Long, val title: String, val translation: String?)
data class LessonDetailDto(val id: Long, val title: String, val content: String?, val topicId: Long, val phrases: List<PhraseDto>)
/** API content can contain placeholder phrase rows without text; those are excluded before Room persistence. */
data class PhraseDto(val id: Long, val text: String?, val translation: String?)
