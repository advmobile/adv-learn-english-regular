package org.callapp.regularenglish

import android.os.Bundle
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.callapp.regularenglish.data.repository.ContentSyncRepository
import org.callapp.regularenglish.data.repository.LearningProgressRepository
import org.callapp.regularenglish.data.repository.LearningAnalyticsRepository
import org.callapp.regularenglish.reminder.ReminderSettingsRepository
import org.callapp.regularenglish.data.repository.LanguageSettingsRepository
import org.callapp.regularenglish.domain.repository.ContentRepository
import org.callapp.regularenglish.ui.navigation.RegularEnglishNavGraph
import org.callapp.regularenglish.ui.screens.SplashScreen
import org.callapp.regularenglish.ads.AdMobManager
import org.callapp.regularenglish.ads.ConsentManager
import org.callapp.regularenglish.ui.theme.LearnEnglishRegularTheme
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    companion object { const val EXTRA_NOTIFICATION_LESSON_ID = "notification_lesson_id" }
    @Inject lateinit var contentSyncRepository: ContentSyncRepository
    @Inject lateinit var contentRepository: ContentRepository
    @Inject lateinit var learningProgressRepository: LearningProgressRepository
    @Inject lateinit var learningAnalyticsRepository: LearningAnalyticsRepository
    @Inject lateinit var reminderSettingsRepository: ReminderSettingsRepository
    @Inject lateinit var languageSettingsRepository: LanguageSettingsRepository
    private var splashFinished by mutableStateOf(false)
    private var notificationLessonId by mutableStateOf<String?>(null)
    private val notificationPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        notificationLessonId = intent.getStringExtra(EXTRA_NOTIFICATION_LESSON_ID)
        lifecycleScope.launch { reminderSettingsRepository.ensureDefaultEnabled() }
        ConsentManager.gatherConsent(this) { canRequestAds -> if (canRequestAds) AdMobManager.initialize(application) }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
        setContent {
            LearnEnglishRegularTheme {
                if (!splashFinished) SplashScreen { splashFinished = true }
                else RegularEnglishNavGraph(contentRepository, contentSyncRepository, learningProgressRepository, learningAnalyticsRepository, reminderSettingsRepository, languageSettingsRepository, notificationLessonId)
            }
        }
    }

    override fun onNewIntent(intent: android.content.Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        notificationLessonId = intent.getStringExtra(EXTRA_NOTIFICATION_LESSON_ID)
    }
}
