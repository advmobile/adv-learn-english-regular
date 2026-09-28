package org.callapp.regularenglish.domain.model

data class Topic(val id: String, val title: String, val icon: String, val description: String)
data class Lesson(val id: String, val topicId: String, val title: String, val description: String, val content: String, val progress: Int)
data class Vocabulary(val id: Long, val word: String, val ipa: String, val meaning: String, val example: String, val lessonId: String = "")
data class LearningStats(val streak: Int = 7, val xp: Int = 350, val dailyProgress: Int = 6)
