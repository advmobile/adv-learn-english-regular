package org.callapp.regularenglish.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import org.callapp.regularenglish.R
import org.callapp.regularenglish.domain.repository.ContentRepository
import org.callapp.regularenglish.data.repository.ContentSyncRepository
import org.callapp.regularenglish.data.repository.LearningProgressRepository

data class TopicUi(val id: String, val title: String, val subtitle: String, val icon: String, val lessons: List<LessonUi>)
data class LessonUi(val id: String = "", val title: String, val subtitle: String, val progress: Int = 0, val locked: Boolean = false)

@Composable
fun TopicsScreen(repository: ContentRepository, onBack: () -> Unit, onOpenTopic: (String) -> Unit) {
    val remoteTopics by repository.observeTopics().collectAsState(initial = emptyList())
    AppPage { padding -> LazyColumn(Modifier.fillMaxSize().padding(padding).padding(horizontal = 20.dp), contentPadding = PaddingValues(top = 18.dp, bottom = 28.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item { Text("‹  Back", color = Primary, fontWeight = FontWeight.Bold, modifier = Modifier.clickable { onBack() }) }
        item { Text("Choose a topic", color = Ink, fontWeight = FontWeight.ExtraBold, fontSize = 28.sp) }
        item { Text("Each topic contains short lessons for your learning path.", color = MutedInk, fontSize = 14.sp) }
        item { InlineAdBanner() }
        items(remoteTopics.size) { index -> val topic = remoteTopics[index]; WhiteCard(Modifier.fillMaxWidth().roundClick { onOpenTopic(topic.id) }) { Row(Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) { Surface(Modifier.size(52.dp), shape = ControlShape, color = if (index == 0) Emerald.copy(alpha = .15f) else SurfaceContainer) { Text("🎧", Modifier.padding(top = 13.dp).fillMaxWidth(), fontSize = 24.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center) }; Column(Modifier.weight(1f).padding(start = 14.dp)) { Text(topic.title, color = Ink, fontWeight = FontWeight.ExtraBold, fontSize = 17.sp); Text(topic.description, color = MutedInk, fontSize = 13.sp); Text("OFFLINE LESSONS", color = TealDark, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 6.dp)) }; Text("›", color = Primary, fontSize = 28.sp) } } }
    } }
}

@Composable
fun TopicLessonsScreen(repository: ContentRepository, syncRepository: ContentSyncRepository, progressRepository: LearningProgressRepository, topicId: String, onBack: () -> Unit, onOpenLesson: (LessonUi) -> Unit) {
    val lessons by repository.observeLessons(topicId).collectAsState(initial = emptyList())
    val progress by progressRepository.observeAll().collectAsState(initial = emptyList())
    val isLoading = remember(topicId) { mutableStateOf(true) }
    LaunchedEffect(topicId) {
        syncRepository.ensureTopicLessons(topicId)
        isLoading.value = false
    }
    AppPage { padding -> LazyColumn(Modifier.fillMaxSize().padding(padding).padding(horizontal = 20.dp), contentPadding = PaddingValues(top = 18.dp, bottom = 28.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item { Text("‹  ${stringResource(R.string.topic)}", color = Primary, fontWeight = FontWeight.Bold, modifier = Modifier.clickable { onBack() }) }
        item { Text("🎧", fontSize = 40.sp) }
        item { Text("Listening Lessons", color = Ink, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold) }
        item { Text(stringResource(R.string.offline_lessons), color = MutedInk, fontSize = 14.sp) }
        item { InlineAdBanner() }
        if (isLoading.value && lessons.isEmpty()) item { Text("Loading lessons…", color = Primary, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth(), textAlign = androidx.compose.ui.text.style.TextAlign.Center) }
        if (!isLoading.value && lessons.isEmpty()) item { Text("Unable to load lessons. Check your internet connection and open this topic again.", color = MutedInk, modifier = Modifier.fillMaxWidth(), textAlign = androidx.compose.ui.text.style.TextAlign.Center) }
        items(lessons.size) { index -> val row = lessons[index]; val lesson = LessonUi(row.id, row.title, row.description, progress.firstOrNull { it.lessonId == row.id }?.completionPercentage ?: 0); WhiteCard(Modifier.fillMaxWidth().roundClick { onOpenLesson(lesson) }) { LessonRow(index, lesson) } }
    } }
}

@Composable
private fun LessonRow(index: Int, lesson: LessonUi) {
    Row(Modifier.padding(horizontal = 16.dp, vertical = 14.dp), verticalAlignment = Alignment.CenterVertically) {
        Surface(Modifier.size(44.dp), shape = ControlShape, color = Emerald.copy(alpha = .15f)) { Text("${index + 1}", Modifier.fillMaxSize().wrapContentHeight(Alignment.CenterVertically), textAlign = androidx.compose.ui.text.style.TextAlign.Center, color = Primary, fontWeight = FontWeight.ExtraBold, fontSize = 17.sp) }
        Column(Modifier.weight(1f).padding(start = 13.dp)) { Text(lesson.title, color = Ink, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, maxLines = 1); if (lesson.subtitle.isNotBlank()) Text(lesson.subtitle, color = MutedInk, fontSize = 12.sp, maxLines = 2, modifier = Modifier.padding(top = 3.dp)); Text(if (lesson.progress == 100) "✓ ${stringResource(R.string.completed)}" else if (lesson.progress > 0) "${lesson.progress}% ${stringResource(R.string.completed)}" else stringResource(R.string.start_learning_lesson), color = if (lesson.progress == 100) Emerald else TealDark, fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.padding(top = 7.dp)) }
        Surface(Modifier.size(32.dp), shape = androidx.compose.foundation.shape.CircleShape, color = Mint) { Text("›", Modifier.fillMaxSize().wrapContentHeight(Alignment.CenterVertically), textAlign = androidx.compose.ui.text.style.TextAlign.Center, color = Primary, fontSize = 22.sp, fontWeight = FontWeight.Bold) }
    }
}
