package org.callapp.regularenglish.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import org.callapp.regularenglish.data.repository.LearningProgressRepository
import org.callapp.regularenglish.domain.repository.ContentRepository
import org.callapp.regularenglish.data.repository.LearningAnalyticsRepository
import androidx.compose.ui.res.stringResource
import org.callapp.regularenglish.R
import org.callapp.regularenglish.ads.AdMobBanner

@Composable
fun HomeScreen(repository: ContentRepository, progressRepository: LearningProgressRepository, analyticsRepository: LearningAnalyticsRepository, onOpenLesson: (String) -> Unit, onOpenProfile: () -> Unit, onOpenTopics: (String) -> Unit, onPractice: () -> Unit, onReview: () -> Unit) {
    val progress by progressRepository.observeAll().collectAsState(initial = emptyList())
    val topics by repository.observeTopics().collectAsState(initial = emptyList())
    val lessons by repository.observeAllLessons().collectAsState(initial = emptyList())
    val completed = progress.count { it.isCompleted }
    val xp = progress.sumOf { it.starsEarned }
    val current = progress.filter { !it.isCompleted }.maxByOrNull { it.lastStudiedAt }?.let { row -> lessons.firstOrNull { it.id == row.lessonId } } ?: lessons.firstOrNull()
    val analytics by analyticsRepository.observe().collectAsState(initial = org.callapp.regularenglish.data.repository.LearningAnalytics(0, 0, 0, 0, 10))
    AppPage(showBanner = false) { padding ->
        Box(Modifier.fillMaxSize()) {
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 20.dp), contentPadding = PaddingValues(top = 12.dp, bottom = 190.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(shape = AppCardShape, color = SurfaceWhite) { Text("🌱", Modifier.padding(9.dp), fontSize = 18.sp) }
                    Column(Modifier.weight(1f).padding(start = 8.dp)) { Text("English Regular", color = Primary, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp); Text("Home", color = MutedInk, fontSize = 11.sp) }
                    Surface(shape = AppCardShape, color = AmberContainer) { Text("🔥 7d", Modifier.padding(horizontal = 9.dp, vertical = 6.dp), color = AmberText, fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                }
            }
            item { Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) { Metric("🔥", "${analytics.streak}", stringResource(R.string.days)); Metric("⚡", "${analytics.totalXp} XP", stringResource(R.string.earned)); Metric("▤", "${analytics.masteredWords}", stringResource(R.string.mastered_words)) } }
            item {
                Surface(Modifier.fillMaxWidth(), shape = ControlShape, color = AmberSoft.copy(alpha = .4f)) { Row(Modifier.padding(13.dp), verticalAlignment = Alignment.CenterVertically) { Text("✓", Modifier.background(Gold.copy(alpha = .4f), ControlShape).padding(8.dp), color = AmberText, fontWeight = FontWeight.Bold); Column(Modifier.weight(1f).padding(start = 10.dp)) { Text(stringResource(R.string.today_goal), color = AmberText, fontWeight = FontWeight.Bold, fontSize = 11.sp); Text(stringResource(R.string.learn_minutes_daily, analytics.dailyGoalMinutes), color = Ink, fontSize = 13.sp) }; Text("${analytics.todayMinutes}/${analytics.dailyGoalMinutes}", color = AmberText, fontWeight = FontWeight.Bold, fontSize = 12.sp) } }
            }
            item {
                current?.let { lesson -> WhiteCard(Modifier.fillMaxWidth().roundClick { onOpenLesson(lesson.id) }) { Column(Modifier.padding(20.dp)) { Text("● ${stringResource(R.string.continue_learning).uppercase()}", color = Primary, fontWeight = FontWeight.Bold, fontSize = 11.sp); Text(lesson.title, color = Ink, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, modifier = Modifier.padding(top = 14.dp)); Text(lesson.description, color = MutedInk, fontSize = 13.sp, modifier = Modifier.padding(top = 4.dp)); Button(onClick = { onOpenLesson(lesson.id) }, shape = AppCardShape, colors = ButtonDefaults.buttonColors(containerColor = Primary), modifier = Modifier.padding(top = 16.dp)) { Text("${stringResource(R.string.continue_learning)}  ▶", fontSize = 12.sp, fontWeight = FontWeight.Bold) } } } }
            }
            item { InlineAdBanner() }
            item { Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) { Column(Modifier.weight(1f)) { Text(stringResource(R.string.basic_path), color = Forest, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp); Text(stringResource(R.string.choose_listening_topic), color = MutedInk, fontSize = 13.sp) }; Text("${stringResource(R.string.all)} ›", color = Primary, fontWeight = FontWeight.Bold, fontSize = 13.sp, modifier = Modifier.clickable { onOpenTopics("all") }) } }
            items(topics) { topic -> WhiteCard(Modifier.fillMaxWidth().roundClick { onOpenTopics(topic.id) }) { Row(Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) { Surface(Modifier.size(54.dp), shape = ControlShape, color = Emerald.copy(alpha = .16f)) { Text(topic.icon.ifBlank { "🎧" }, Modifier.padding(top = 14.dp).fillMaxWidth(), textAlign = androidx.compose.ui.text.style.TextAlign.Center, fontSize = 24.sp) }; Column(Modifier.weight(1f).padding(start = 14.dp)) { Text(topic.title, color = Ink, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp); topic.description.takeIf { it.isNotBlank() && it != topic.title }?.let { Text(it, color = MutedInk, fontSize = 12.sp) }; Text(stringResource(R.string.view_lessons), color = TealDark, fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.padding(top = 5.dp)) }; Text("›", color = Primary, fontSize = 24.sp) } } }
            item { Surface(onClick = onOpenProfile, modifier = Modifier.fillMaxWidth(), shape = AppCardShape, color = SurfaceWhite) { Row(Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) { ProfileGlyph(Primary); Text(stringResource(R.string.profile_achievements), Modifier.padding(start = 10.dp), color = Forest, fontWeight = FontWeight.Bold) } } }
        }
        Box(Modifier.align(Alignment.BottomCenter).padding(padding)) { FloatingBottomDock("home", onHome = {}, onPractice = onPractice, onReview = onReview, onProfile = onOpenProfile) }
        }
    }
}

@Composable private fun RowScope.Metric(icon: String, value: String, label: String) {
    WhiteCard(Modifier.weight(1f)) { Column(Modifier.fillMaxWidth().padding(vertical = 12.dp, horizontal = 4.dp), horizontalAlignment = Alignment.CenterHorizontally) { Text(icon, textAlign = androidx.compose.ui.text.style.TextAlign.Center); Text(value, Modifier.fillMaxWidth(), color = Ink, textAlign = androidx.compose.ui.text.style.TextAlign.Center, fontWeight = FontWeight.ExtraBold, fontSize = 13.sp); Text(label, Modifier.fillMaxWidth(), color = MutedInk, textAlign = androidx.compose.ui.text.style.TextAlign.Center, fontSize = 10.sp, maxLines = 1) } }
}
