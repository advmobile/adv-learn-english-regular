package org.callapp.regularenglish.ui.navigation

import android.net.Uri
import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.callapp.regularenglish.ui.screens.HomeScreen
import org.callapp.regularenglish.ui.screens.LessonScreen
import org.callapp.regularenglish.ui.screens.OnboardingScreen
import org.callapp.regularenglish.ui.screens.ProfileScreen
import org.callapp.regularenglish.ui.screens.TopicsScreen
import org.callapp.regularenglish.ui.screens.TopicLessonsScreen
import org.callapp.regularenglish.ui.screens.LanguageSelectionScreen
import org.callapp.regularenglish.ui.screens.InitialContentLoading
import org.callapp.regularenglish.ui.screens.PracticeReviewScreen
import org.callapp.regularenglish.ui.screens.DailyPracticeScreen
import org.callapp.regularenglish.ui.screens.ReviewScreen
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import org.callapp.regularenglish.domain.repository.ContentRepository
import org.callapp.regularenglish.data.repository.ContentSyncRepository
import org.callapp.regularenglish.data.repository.LearningProgressRepository
import org.callapp.regularenglish.data.repository.LearningAnalyticsRepository
import org.callapp.regularenglish.reminder.ReminderSettingsRepository
import org.callapp.regularenglish.data.repository.LanguageSettingsRepository
import org.callapp.regularenglish.ads.AdMobManager

@Composable
fun RegularEnglishNavGraph(contentRepository: ContentRepository, contentSyncRepository: ContentSyncRepository, progressRepository: LearningProgressRepository, analyticsRepository: LearningAnalyticsRepository, reminderRepository: ReminderSettingsRepository, languageRepository: LanguageSettingsRepository, notificationLessonId: String?) {
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    fun navigateAfterAction(navigation: () -> Unit) = AdMobManager.continueAfterScreenAction(context as? Activity, navigation)
    val startDestination = when {
        context.getSharedPreferences("language_settings", android.content.Context.MODE_PRIVATE).getBoolean("selected", false) -> "content_loading"
        else -> "language"
    }
    NavHost(navController = navController, startDestination = startDestination) {
        composable("language") {
            LanguageSelectionScreen { code ->
                scope.launch {
                    context.getSharedPreferences("language_settings", android.content.Context.MODE_PRIVATE)
                        .edit().putBoolean("selected", true).apply()
                    languageRepository.set(code)
                    navController.navigate("content_loading") {
                        popUpTo("language") { inclusive = true }
                    }
                }
            }
        }
        composable("content_loading") {
            InitialContentLoading(contentSyncRepository) {
                val destination = notificationLessonId
                    ?.takeIf { it.isNotBlank() }
                    ?.let { "lesson?lessonId=${Uri.encode(it)}" }
                    ?: "onboarding"
                navController.navigate(destination) {
                    popUpTo("content_loading") { inclusive = true }
                }
            }
        }
        composable("onboarding") { OnboardingScreen(analyticsRepository, languageRepository) { navigateAfterAction { navController.navigate("home") { popUpTo("onboarding") { inclusive = true } } } } }
        composable("home") { HomeScreen(contentRepository, progressRepository, analyticsRepository, { lessonId -> navigateAfterAction { navController.navigate("lesson?lessonId=$lessonId") } }, { navigateAfterAction { navController.navigate("profile") } }, { topicId -> navigateAfterAction { if (topicId == "all") navController.navigate("topics") else navController.navigate("topic/$topicId") } }, { navigateAfterAction { navController.navigate("practice") } }, { navigateAfterAction { navController.navigate("review") } }) }
        composable("practice") { DailyPracticeScreen(contentRepository, progressRepository, analyticsRepository, { navigateAfterAction { navController.navigate("home") } }, {}, { navigateAfterAction { navController.navigate("review") } }, { navigateAfterAction { navController.navigate("topics") } }, { navigateAfterAction { navController.navigate("profile") } }) }
        composable("review") { ReviewScreen(contentRepository, analyticsRepository, { navigateAfterAction { navController.navigate("home") } }, { navigateAfterAction { navController.navigate("practice") } }, {}, { navigateAfterAction { navController.navigate("topics") } }, { navigateAfterAction { navController.navigate("profile") } }) }
        composable("topics") { TopicsScreen(contentRepository, onBack = { navController.popBackStack() }, onOpenTopic = { navigateAfterAction { navController.navigate("topic/$it") } }) }
        composable("topic/{topicId}") { entry -> TopicLessonsScreen(contentRepository, contentSyncRepository, progressRepository, entry.arguments?.getString("topicId").orEmpty(), onBack = { navController.popBackStack() }, onOpenLesson = { lesson -> navigateAfterAction { navController.navigate("lesson?lessonId=${lesson.id}") } }) }
        composable("lesson?lessonId={lessonId}", arguments = listOf(navArgument("lessonId") { type = NavType.StringType; defaultValue = "" })) { entry -> LessonScreen(contentRepository, progressRepository, analyticsRepository, onBack = { navController.popBackStack() }, lessonId = entry.arguments?.getString("lessonId").orEmpty()) }
        composable("profile") { ProfileScreen(analyticsRepository, reminderRepository, languageRepository, contentSyncRepository, onHome = { navigateAfterAction { navController.navigate("home") } }, onPractice = { navigateAfterAction { navController.navigate("practice") } }, onReview = { navigateAfterAction { navController.navigate("review") } }) }
    }
}
