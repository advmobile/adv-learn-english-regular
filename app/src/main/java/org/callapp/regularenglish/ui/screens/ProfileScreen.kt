package org.callapp.regularenglish.ui.screens

import android.app.Activity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.Surface
import androidx.compose.material3.TextButton
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.platform.LocalContext
import org.callapp.regularenglish.R
import org.callapp.regularenglish.data.repository.LearningAnalyticsRepository
import org.callapp.regularenglish.reminder.ReminderSettingsRepository
import org.callapp.regularenglish.data.repository.LanguageSettingsRepository
import org.callapp.regularenglish.data.repository.ContentSyncRepository
import kotlinx.coroutines.launch
import org.callapp.regularenglish.ads.ConsentManager

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    analyticsRepository: LearningAnalyticsRepository,
    reminderRepository: ReminderSettingsRepository,
    languageRepository: LanguageSettingsRepository,
    contentSyncRepository: ContentSyncRepository,
    onHome: () -> Unit,
    onPractice: () -> Unit,
    onReview: () -> Unit
) {
    val analytics by analyticsRepository.observe().collectAsState(
        initial = org.callapp.regularenglish.data.repository.LearningAnalytics(
            0,
            0,
            0,
            0,
            10
        )
    )
    val reminder by reminderRepository.observe().collectAsState(initial = null)
    val language by languageRepository.observe().collectAsState(initial = null)
    val syncProgress by contentSyncRepository.progress.collectAsState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val activity = context as? Activity
    val privacyChoicesRequired =
        remember(context) { ConsentManager.isPrivacyChoicesRequired(context) }
    val showTimePicker = remember { mutableStateOf(false) }
    val languageMenuExpanded = remember { mutableStateOf(false) }
    val timeState = rememberTimePickerState(
        initialHour = reminder?.hour ?: 20,
        initialMinute = reminder?.minute ?: 0,
        is24Hour = true
    )
    AppPage(showBanner = false) { padding ->
        Box(Modifier.fillMaxSize()) {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 20.dp)
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 102.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(18.dp)); Surface(
                Modifier.size(96.dp),
                shape = androidx.compose.foundation.shape.CircleShape,
                color = SoftAccent
            ) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    ProfileGlyph(
                        Primary,
                        Modifier.size(50.dp)
                    )
                }
            }
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ProfileMetric("🔥", "${analytics.streak}", "Streak days"); ProfileMetric(
                    "⚡",
                    "${analytics.totalXp}",
                    "Total XP"
                ); ProfileMetric("▤", "${analytics.masteredWords}", "Mastered")
                }
                WhiteCard(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp)
                ) {
                    Column(Modifier.padding(20.dp)) {
                        Text(
                            "Today's goal",
                            color = Ink,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 17.sp
                        ); Text(
                        "${analytics.todayMinutes} / ${analytics.dailyGoalMinutes} min",
                        color = Emerald,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(top = 12.dp)
                    ); Text(
                        if (analytics.todayMinutes >= analytics.dailyGoalMinutes) "You have completed today's goal!" else "${analytics.dailyGoalMinutes - analytics.todayMinutes} minutes left to reach your goal",
                        color = TealDark,
                        fontSize = 12.sp
                    )
                    }
                }
                Text(
                    "ACHIEVEMENTS",
                    color = Forest,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp)
                )
                WhiteCard(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                ) {
                    Column(
                        Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            if (analytics.masteredWords > 0) "✓  ${analytics.masteredWords} words/phrases mastered" else "○  Rate flashcards to build your mastered words",
                            color = Ink,
                            fontWeight = FontWeight.Medium
                        ); Text(
                        if (analytics.streak > 0) "🔥  ${analytics.streak}-day learning streak" else "○  Complete your first learning activity to begin a streak",
                        color = Ink,
                        fontWeight = FontWeight.Medium
                    ); Text(
                        if (analytics.todayMinutes >= analytics.dailyGoalMinutes) "★  Today's goal completed" else "○  ${analytics.dailyGoalMinutes - analytics.todayMinutes} minutes left for today's goal",
                        color = if (analytics.todayMinutes >= analytics.dailyGoalMinutes) Gold else MutedInk,
                        fontWeight = FontWeight.Medium
                    )
                    }
                }
                Text(
                    stringResource(R.string.settings).uppercase(),
                    color = Forest,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp)
                )
                WhiteCard(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                        .roundClick { showTimePicker.value = true }) {
                    Row(
                        Modifier.padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ProfileGlyph(Primary); Column(
                        Modifier
                            .weight(1f)
                            .padding(start = 12.dp)
                    ) {
                        Text(
                            stringResource(R.string.daily_reminder),
                            color = Ink,
                            fontWeight = FontWeight.Bold
                        ); Text(reminder?.takeIf { it.enabled }?.let {
                        "${stringResource(R.string.daily_reminder)} • %02d:%02d".format(
                            it.hour,
                            it.minute
                        )
                    } ?: stringResource(R.string.reminder_off),
                        color = MutedInk,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 3.dp))
                    }; androidx.compose.material3.Switch(
                        checked = reminder?.enabled ?: true,
                        onCheckedChange = { enabled ->
                            scope.launch {
                                reminderRepository.save(
                                    enabled,
                                    reminder?.hour ?: 20,
                                    reminder?.minute ?: 0
                                )
                            }
                        })
                    }
                }
                if (privacyChoicesRequired) WhiteCard(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                        .roundClick { activity?.let(ConsentManager::showPrivacyChoices) }) {
                    Row(
                        Modifier.padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("◌", color = Primary, fontSize = 22.sp); Column(
                        Modifier
                            .weight(1f)
                            .padding(start = 12.dp)
                    ) {
                        Text(
                            "Privacy choices",
                            color = Ink,
                            fontWeight = FontWeight.Medium
                        ); Text(
                        "Manage advertising privacy preferences",
                        color = MutedInk,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 3.dp)
                    )
                    }; Text("›", color = Primary, fontSize = 22.sp)
                    }
                }
                if (showTimePicker.value) AlertDialog(
                    onDismissRequest = {
                        showTimePicker.value = false
                    },
                    title = { Text("Choose reminder time") },
                    text = { TimePicker(timeState) },
                    confirmButton = {
                        TextButton(onClick = {
                            scope.launch {
                                reminderRepository.save(
                                    true,
                                    timeState.hour,
                                    timeState.minute
                                )
                            }; showTimePicker.value = false
                        }) { Text("Save") }
                    },
                    dismissButton = {
                        TextButton(onClick = { showTimePicker.value = false }) {
                            Text("Cancel")
                        }
                    })
                Text(
                    "NATIVE LANGUAGE",
                    color = Forest,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp)
                )
                Box(Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)) {
                    WhiteCard(
                        Modifier
                            .fillMaxWidth()
                            .roundClick { languageMenuExpanded.value = true }) {
                        Row(
                            Modifier.padding(18.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ProfileGlyph(Primary); Column(
                            Modifier
                                .weight(1f)
                                .padding(start = 12.dp)
                        ) {
                            Text(
                                "Lesson translation: ${language?.language?.uppercase() ?: "VI"}",
                                color = Ink,
                                fontWeight = FontWeight.Medium
                            ); Text(
                            if (syncProgress.isRunning) syncProgress.message else "Choose again to download lessons for offline learning.",
                            color = MutedInk,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 3.dp)
                        )
                        }; Text("⌄", color = Primary, fontSize = 22.sp)
                        }
                    }; androidx.compose.material3.DropdownMenu(
                    expanded = languageMenuExpanded.value,
                    onDismissRequest = { languageMenuExpanded.value = false }) {
                    listOf(
                        "vi" to "Tiếng Việt",
                        "fr" to "Français",
                        "pt" to "Português",
                        "ja" to "日本語",
                        "ko" to "한국어",
                        "ru" to "Русский",
                        "zh" to "中文",
                        "de" to "Deutsch",
                        "es" to "Español"
                    ).forEach { (code, label) ->
                        androidx.compose.material3.DropdownMenuItem(
                            text = {
                                Text(
                                    label
                                )
                            },
                            onClick = {
                                scope.launch { languageRepository.set(code); contentSyncRepository.syncInitialContent() }; languageMenuExpanded.value =
                                false
                            })
                    }
                }
                }
            }; Box(
            Modifier
                .align(Alignment.BottomCenter)
                .padding(padding)
        ) {
            FloatingBottomDock(
                "profile",
                onHome = onHome,
                onPractice = onPractice,
                onReview = onReview,
                onProfile = {})
        }
        }
    }
}

@Composable
private fun RowScope.ProfileMetric(icon: String, value: String, label: String) {
    WhiteCard(Modifier.weight(1f)) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                icon,
                Modifier.fillMaxWidth(),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            ); Text(
            value,
            Modifier.fillMaxWidth(),
            color = Ink,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp
        ); Text(
            label,
            Modifier.fillMaxWidth(),
            color = MutedInk,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            fontSize = 9.sp,
            maxLines = 1
        )
        }
    }
}
