package org.callapp.regularenglish.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import org.callapp.regularenglish.data.repository.LearningAnalyticsRepository
import org.callapp.regularenglish.data.repository.LanguageSettingsRepository
import kotlinx.coroutines.launch
import androidx.compose.ui.res.stringResource
import org.callapp.regularenglish.R

@Composable
fun OnboardingScreen(analyticsRepository: LearningAnalyticsRepository, languageRepository: LanguageSettingsRepository, onStart: () -> Unit) {
    val analytics by analyticsRepository.observe().collectAsState(initial = org.callapp.regularenglish.data.repository.LearningAnalytics(0, 0, 0, 0, 10))
    val scope = rememberCoroutineScope()
    var selectedMinutes by remember { mutableIntStateOf(10) }
    LaunchedEffect(analytics.dailyGoalMinutes) { selectedMinutes = analytics.dailyGoalMinutes }
    val language by languageRepository.observe().collectAsState(initial = null)
    var selectedLanguage by remember { mutableStateOf("vi") }
    LaunchedEffect(language?.language) { selectedLanguage = language?.language ?: "vi" }
    AppPage { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(horizontal = 20.dp).verticalScroll(rememberScrollState()).padding(bottom = 28.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(Modifier.fillMaxWidth().padding(top = 12.dp), verticalAlignment = Alignment.CenterVertically) { Surface(Modifier.size(44.dp), shape = RoundedCornerShape(16.dp), color = SurfaceWhite) { Box(contentAlignment = Alignment.Center) { Text("📘") } }; Spacer(Modifier.width(10.dp)); Column(Modifier.weight(1f)) { Text("Learn English Regular", color = Ink, fontWeight = FontWeight.Bold, fontSize = 16.sp); Text("Build steady English habits every day", color = TealDark, fontSize = 11.sp) }; Surface(shape = CircleShape, color = SurfaceWhite) { Text("★ A1–A2", Modifier.padding(horizontal = 10.dp, vertical = 7.dp), fontSize = 11.sp, fontWeight = FontWeight.Bold) } }
            WhiteCard(Modifier.fillMaxWidth()) { Column(Modifier.padding(20.dp)) { Surface(shape = CircleShape, color = Mint) { Text("🌱  NATURAL START", Modifier.padding(horizontal = 12.dp, vertical = 6.dp), color = TealDark, fontSize = 11.sp, fontWeight = FontWeight.Bold) }; Text("Essential English for beginners", Modifier.padding(top = 14.dp), color = Ink, fontWeight = FontWeight.ExtraBold, fontSize = 24.sp, lineHeight = 30.sp); Text("Build listening and speaking confidence through short, visual daily lessons.", Modifier.padding(top = 7.dp), color = MutedInk, fontSize = 15.sp, lineHeight = 22.sp); Text("📖", Modifier.fillMaxWidth().padding(top = 16.dp).background(SoftAccent, RoundedCornerShape(16.dp)).padding(vertical = 20.dp), textAlign = TextAlign.Center, fontSize = 54.sp); Text("500+ short, memorable lessons", Modifier.fillMaxWidth().padding(top = 7.dp), color = Forest, textAlign = TextAlign.Center, fontSize = 13.sp, fontWeight = FontWeight.SemiBold) } }
            InlineAdBanner()
            Column { Text(stringResource(R.string.daily_goal), color = Ink, fontWeight = FontWeight.Bold, fontSize = 20.sp); Text("Choose a pace that fits your routine", color = MutedInk, fontSize = 13.sp) }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(5, 10, 15, 30).forEach { minutes ->
                    Surface(color = if (minutes == selectedMinutes) Mint else SurfaceWhite, shape = RoundedCornerShape(16.dp), modifier = Modifier.weight(1f).height(108.dp).clickable { selectedMinutes = minutes }) {
                        Column(Modifier.fillMaxWidth().padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.SpaceBetween) {
                            Text(if (minutes == 10) "🌿" else "🌱", textAlign = TextAlign.Center)
                            Text("$minutes min\n/ day", modifier = Modifier.fillMaxWidth(), color = if (minutes == selectedMinutes) Forest else Ink, textAlign = TextAlign.Center, fontWeight = FontWeight.Bold, fontSize = 11.sp, lineHeight = 14.sp)
                            Text(if (minutes == 10) "Balanced" else "Focused", modifier = Modifier.fillMaxWidth(), color = if (minutes == selectedMinutes) TealDark else MutedInk, textAlign = TextAlign.Center, fontSize = 9.sp)
                        }
                    }
                }
            }
            Column { Text("Your current level", color = Ink, fontWeight = FontWeight.Bold, fontSize = 20.sp); Text("Help us personalize your first lesson", color = MutedInk, fontSize = 13.sp) }
            WhiteCard(Modifier.fillMaxWidth()) { Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) { Text("✿", Modifier.size(40.dp).background(Emerald, RoundedCornerShape(12.dp)).padding(top = 10.dp), color = Color.White, textAlign = TextAlign.Center); Spacer(Modifier.width(12.dp)); Column(Modifier.weight(1f)) { Text("Starting from zero", color = Forest, fontWeight = FontWeight.Bold, fontSize = 15.sp); Text("Learn the alphabet, pronunciation, and numbers", color = TealDark, fontSize = 11.sp) }; Text("●", color = Emerald) } }
            WhiteCard(Modifier.fillMaxWidth()) { Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) { Text("▤", Modifier.size(40.dp).background(SurfaceContainer, RoundedCornerShape(12.dp)).padding(top = 10.dp), textAlign = TextAlign.Center); Spacer(Modifier.width(12.dp)); Column(Modifier.weight(1f)) { Text("I know basic words and phrases", color = Ink, fontWeight = FontWeight.Bold, fontSize = 15.sp); Text("Review quickly and build everyday sentences", color = MutedInk, fontSize = 11.sp) }; Text("○", color = MutedInk) } }
            WhiteCard(Modifier.fillMaxWidth()) { Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) { Text("🔥", fontSize = 25.sp); Spacer(Modifier.width(10.dp)); Column(Modifier.weight(1f)) { Text("Build a learning streak", color = Ink, fontWeight = FontWeight.Bold, fontSize = 13.sp); Text("Complete a learning activity every day", color = MutedInk, fontSize = 11.sp) }; Text("+10 XP / day", color = TealDark, fontWeight = FontWeight.Bold, fontSize = 11.sp) } }
            Button(onClick = { scope.launch { analyticsRepository.setDailyGoal(selectedMinutes); onStart() } }, modifier = Modifier.fillMaxWidth().height(56.dp), shape = CircleShape, colors = ButtonDefaults.buttonColors(containerColor = Emerald)) { Text("${stringResource(R.string.start_learning)}   →", fontWeight = FontWeight.ExtraBold, fontSize = 14.sp) }
        }
    }
}
