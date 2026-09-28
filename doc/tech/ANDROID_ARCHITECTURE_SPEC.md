# 🏗️ ANDROID ARCHITECTURE & COMPOSE DESIGN SYSTEM SPECIFICATION

> **Dành cho:** AI Coding Agent & Mobile Developers  
> **Nền tảng:** Android Native (Kotlin 2.x + Jetpack Compose + Material 3 + Room Asset SQLite)  
> **Nhóm ứng dụng:** AdvMobile Modular Apps (Learn English Basic, Business, Travel, Interview, Pronunciation, Grammar, Regular, Idioms)  
> **Mục tiêu:** Cung cấp tài liệu thiết kế kiến trúc chuẩn Clean Architecture, khả năng tái sử dụng mã nguồn 100% khi clone ứng dụng mới, và bản thiết kế hệ thống UI Material 3 khớp với Google Stitch Specs.

---

## 📐 1. TỔNG QUAN KIẾN TRÚC & PHÂN CẤP MODULAR (ARCHITECTURE OVERVIEW)

Ứng dụng tuân theo mô hình **Clean Architecture + MVVM (Model-View-ViewModel)** với định hướng **Modular & Config-Driven Base App**.

```
                           ┌────────────────────────────────────────┐
                           │            App Configuration           │
                           │   (AppConfig.kt + Assets SQLite DB)    │
                           └──────────────────┬─────────────────────┘
                                              │
┌─────────────────────────────────────────────▼─────────────────────────────────────────────┐
│                                   PRESENTATION LAYER                                      │
│  ┌─────────────────────────────────────────────────────────────────────────────────────┐  │
│  │                            Jetpack Compose UI Screens                               │  │
│  │   [OnboardingScreen] [HomeScreen] [TopicDetailScreen] [LessonMultiSkillEngineScreen]│  │
│  │   [ProfileScreen] [SettingsScreen]                                                  │  │
│  └──────────────────────────────────────────▲──────────────────────────────────────────┘  │
│                                             │ (StateFlow / UiState)                       │
│  ┌──────────────────────────────────────────┴──────────────────────────────────────────┐  │
│  │                                 ViewModels (Hilt)                                   │  │
│  │   [HomeViewModel] [LessonViewModel] [PracticeViewModel] [SpeakingViewModel]           │  │
│  └──────────────────────────────────────────▲──────────────────────────────────────────┘  │
└─────────────────────────────────────────────┼─────────────────────────────────────────────┘
                                              │ (Executes UseCases)
┌─────────────────────────────────────────────┴─────────────────────────────────────────────┐
│                                      DOMAIN LAYER                                         │
│  ┌─────────────────────────────────────────────────────────────────────────────────────┐  │
│  │                                 Domain UseCases                                     │  │
│  │   [GetTopicsUseCase] [GetLessonDetailsUseCase] [UpdateSrsMemoryUseCase]             │  │
│  │   [SaveUserProgressUseCase] [CalculateStreakUseCase]                                │  │
│  └──────────────────────────────────────────▲──────────────────────────────────────────┘  │
│                                             │ (Interface Contracts)                       │
│  ┌──────────────────────────────────────────┴──────────────────────────────────────────┐  │
│  │                                 Domain Repositories                                 │  │
│  │   [ContentRepository] [ProgressRepository] [AudioRepository]                        │  │
│  └──────────────────────────────────────────▲──────────────────────────────────────────┘  │
└─────────────────────────────────────────────┼─────────────────────────────────────────────┘
                                              │ (Data Mapping)
┌─────────────────────────────────────────────┴─────────────────────────────────────────────┐
│                                       DATA LAYER                                          │
│  ┌─────────────────────────────────────────────────────────────────────────────────────┐  │
│  │                      Repository Implementations (Offline-First)                     │  │
│  │   [ContentRepositoryImpl] [ProgressRepositoryImpl]                                  │  │
│  └──────────────────────────────────────────▲──────────────────────────────────────────┘  │
│                                             │                                             │
│  ┌──────────────────────────────────────────┴──────────────────────────────────────────┐  │
│  │                          Room Pre-Packaged Asset Database                           │  │
│  │   [AppDatabase.createFromAsset("database/adv_content.db")]                           │  │
│  │   - TopicDao   - LessonDao   - FlashcardDao   - ProgressDao                         │  │
│  └─────────────────────────────────────────────────────────────────────────────────────┘  │
└───────────────────────────────────────────────────────────────────────────────────────────┘
```

---

## 📁 2. CẤU TRÚC PACKAGE CHUẨN TRONG ANDROID STUDIO (PACKAGE STRUCTURE)

```text
com.speakhub.advmobile.template/
├── App.kt                          # Application Class (@HiltAndroidApp)
├── config/                         # 🟢 CHỈ CẦN THAY ĐỔI THƯ MỤC NÀY KHI CLONE APP MỚI
│   ├── AppConfig.kt                # App ID, App Name, Asset DB Name, Colors, Icons
│   └── AppThemeConfig.kt           # Mapping Dynamic Colors & Material 3 Scheme
├── di/                             # Dependency Injection (Hilt Modules)
│   ├── DatabaseModule.kt           # Room Database Provider (.createFromAsset)
│   ├── RepositoryModule.kt         # Hilt Binds Repository Implementations
│   └── AudioModule.kt              # ExoPlayer & Android SpeechRecognizer Provider
├── data/                           # Data Layer
│   ├── local/
│   │   ├── AppDatabase.kt          # RoomDatabase class
│   │   ├── dao/                    # Room DAOs (TopicDao, LessonDao, etc.)
│   │   └── entity/                 # Room Entities (TopicEntity, LessonEntity, etc.)
│   └── repository/                 # Repository Implementations
├── domain/                         # Domain Layer
│   ├── model/                      # Pure Domain Models (Topic, Lesson, Flashcard)
│   ├── repository/                 # Repository Interfaces
│   └── usecase/                    # Pure Kotlin UseCases
├── ui/                             # Presentation Layer (Jetpack Compose)
│   ├── theme/                      # Material 3 Color, Type, Shape & Glassmorphism
│   ├── components/                 # Shared Composables (TopBar, Dock, Cards, Badges)
│   ├── engine/                     # 🚀 5 CORE SKILL ENGINES
│   │   ├── learn/                  # 📖 Learn Engine (Magazine & Dialogue Chat)
│   │   ├── practice/               # 🎴 Practice Engine (3D Flip SRS Flashcards)
│   │   ├── listening/              # 🎧 Listening Engine (Waveform & Fill-in-blank)
│   │   ├── speaking/               # 🎙️ Speaking Engine (SpeechRecognizer & Score Ring)
│   │   └── quiz/                   # 🧪 Quiz Engine (Multiple Choice & Drag Drop)
│   ├── screens/                    # Full Màn hình
│   │   ├── onboarding/
│   │   ├── home/
│   │   ├── topic/
│   │   ├── lesson/
│   │   └── profile/
│   └── navigation/                 # Jetpack Compose Navigation Graph
└── util/                           # Helper Classes (AudioPlayer, SpeechAnalyzer, SRSAlgorithm)
```

---

## 🎨 3. MATERIAL 3 DESIGN SYSTEM & DYNAMIC THEME ENGINE

### 3.1 App Configuration Class (`AppConfig.kt`)
Khi clone một dự án mới (ví dụ: `learn-english-business`), bạn chỉ cần cập nhật file `AppConfig.kt` duy nhất này:

```kotlin
package com.speakhub.advmobile.template.config

import androidx.compose.ui.graphics.Color

object AppConfig {
    const val APP_ID = "en-basic"
    const val APP_NAME = "Learn English Basic"
    const val DB_ASSET_PATH = "database/en_basic.db" // File SQLite trong assets/database/
    const val DEFAULT_LANGUAGE = "vi"

    // Material 3 Color Tokens
    object Colors {
        val Primary = Color(0xFF10B981)         // Emerald Green
        val PrimaryContainer = Color(0xFFD1FAE5)
        val Secondary = Color(0xFFF59E0B)       // Warm Amber (Streak & XP)
        val BackgroundLight = Color(0xFFF0FDF4)
        val BackgroundDark = Color(0xFF064E3B)
        val SurfaceLight = Color(0xFFFFFFFF)
        val SurfaceDark = Color(0xFF022C22)
    }
}
```

### 3.2 Dynamic Jetpack Compose Theme (`Theme.kt`)

```kotlin
package com.speakhub.advmobile.template.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import com.speakhub.advmobile.template.config.AppConfig

private val LightColorScheme = lightColorScheme(
    primary = AppConfig.Colors.Primary,
    onPrimary = Color.White,
    primaryContainer = AppConfig.Colors.PrimaryContainer,
    secondary = AppConfig.Colors.Secondary,
    background = AppConfig.Colors.BackgroundLight,
    surface = AppConfig.Colors.SurfaceLight
)

private val DarkColorScheme = darkColorScheme(
    primary = AppConfig.Colors.Primary,
    onPrimary = Color.White,
    secondary = AppConfig.Colors.Secondary,
    background = AppConfig.Colors.BackgroundDark,
    surface = AppConfig.Colors.SurfaceDark
)

@Composable
fun AdvMobileTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AdvTypography,
        shapes = AdvShapes,
        content = content
    )
}
```

---

## ⚡ 4. NAVIGATION GRAPH & UI STATE PATTERN

### 4.1 StateFlow & UiState Standard Pattern

Mọi ViewModel trong ứng dụng đều sử dụng mẫu `UiState` bất biến (Immutable State):

```kotlin
sealed interface LessonUiState {
    object Loading : LessonUiState
    data class Success(
        val lessonTitle: String,
        val currentSkillTab: SkillTab = SkillTab.LEARN,
        val overallProgress: Int = 0,
        val dialogContent: List<DialogLine> = emptyList(),
        val flashcards: List<Flashcard> = emptyList()
    ) : LessonUiState
    data class Error(val message: String) : LessonUiState
}
```

### 4.2 Jetpack Compose Navigation Setup (`NavGraph.kt`)

```kotlin
@Composable
fun AdvMobileNavHost(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToTopic = { topicId ->
                    navController.navigate(Screen.TopicDetail.createRoute(topicId))
                }
            )
        }
        composable(
            route = Screen.TopicDetail.route,
            arguments = listOf(navArgument("topicId") { type = NavType.StringType })
        ) {
            TopicDetailScreen(
                onNavigateToLesson = { lessonId ->
                    navController.navigate(Screen.LessonEngine.createRoute(lessonId))
                }
            )
        }
        composable(
            route = Screen.LessonEngine.route,
            arguments = listOf(navArgument("lessonId") { type = NavType.StringType })
        ) {
            LessonMultiSkillEngineScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
```
