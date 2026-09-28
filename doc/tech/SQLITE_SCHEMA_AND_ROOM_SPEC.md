# 🗄️ PRE-PACKAGED SQLITE ASSET & ROOM DATABASE SPECIFICATION

> **Dành cho:** AI Coding Agent & Data Engineers  
> **Nền tảng:** Android Room Persistence Library + Kotlin Coroutines Flow  
> **Nguồn dữ liệu:** File SQLite đóng gói sẵn trong `assets/database/` (Offline-First 100%, Không gọi API)  
> **Chức năng chính:** Đọc dữ liệu khóa học, bài giảng, từ vựng và lưu tiến độ cá nhân (Streak, XP, SRS Memory Rating) client-side.

---

## 📐 1. TỔNG QUAN HỆ THỐNG CƠ SỞ DỮ LIỆU (DATABASE OVERVIEW)

Ứng dụng hoạt động **hoàn toàn offline**. Khi ứng dụng khởi chạy lần đầu tiên trên thiết bị di động, **Room** sẽ tự động copy file SQLite từ thư mục `assets/database/` (đã được nén và chuẩn bị sẵn) vào thư mục CSDL nội bộ của ứng dụng di động.

```
┌─────────────────────────────────────────────────────────────────────────────────────────┐
│                                 APP ASSETS FOLDER                                       │
│  assets/database/en_basic.db (File SQLite chứa sẵn toàn bộ nội dung khóa học)           │
└──────────────────────────────────────────┬──────────────────────────────────────────────┘
                                           │ (Room.createFromAsset Khởi tạo lần đầu)
┌──────────────────────────────────────────▼──────────────────────────────────────────────┐
│                              LOCAL ROOM DATABASE                                        │
│  /data/data/com.speakhub.advmobile.*/databases/advmobile_app.db                         │
│                                                                                         │
│  ┌───────────────────────┐   ┌───────────────────────┐   ┌───────────────────────────┐  │
│  │     Read-Only Content │   │     Read-Only Content │   │ Read/Write User State     │  │
│  │   - topics            │   │   - conversations     │   │   - user_progress         │  │
│  │   - lessons           │   │   - phrases           │   │   - srs_cards             │  │
│  │   - quizzes           │   │   - vocabulary        │   │   - user_stats            │  │
│  └───────────────────────┘   └───────────────────────┘   └───────────────────────────┘  │
└─────────────────────────────────────────────────────────────────────────────────────────┘
```

---

## 📊 2. SƠ ĐỒ BẢNG CƠ SỞ DỮ LIỆU SQLITE (SQLITE SCHEMA)

### 2.1 Bảng `topics` (Danh mục Chủ đề)
```sql
CREATE TABLE IF NOT EXISTS topics (
    id TEXT PRIMARY KEY NOT NULL,
    app_id TEXT NOT NULL,
    title TEXT NOT NULL,
    icon TEXT NOT NULL,
    description TEXT,
    level TEXT NOT NULL DEFAULT 'Beginner',
    display_order INTEGER NOT NULL DEFAULT 0
);
```

### 2.2 Bảng `lessons` (Danh sách Bài học)
```sql
CREATE TABLE IF NOT EXISTS lessons (
    id TEXT PRIMARY KEY NOT NULL,
    topic_id TEXT NOT NULL,
    title TEXT NOT NULL,
    icon TEXT NOT NULL,
    description TEXT,
    content_html TEXT,
    display_order INTEGER NOT NULL DEFAULT 0,
    FOREIGN KEY(topic_id) REFERENCES topics(id) ON DELETE CASCADE
);
```

### 2.3 Bảng `conversations` (Đoạn thoại 2 chiều)
```sql
CREATE TABLE IF NOT EXISTS conversations (
    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    lesson_id TEXT NOT NULL,
    speaker TEXT NOT NULL,
    text_en TEXT NOT NULL,
    translation_native TEXT NOT NULL,
    audio_url TEXT,
    display_order INTEGER NOT NULL DEFAULT 0,
    FOREIGN KEY(lesson_id) REFERENCES lessons(id) ON DELETE CASCADE
);
```

### 2.4 Bảng `vocabulary` (Bộ Từ vựng & Flashcards)
```sql
CREATE TABLE IF NOT EXISTS vocabulary (
    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    lesson_id TEXT NOT NULL,
    word TEXT NOT NULL,
    ipa TEXT NOT NULL,
    meaning TEXT NOT NULL,
    example_en TEXT,
    example_native TEXT,
    part_of_speech TEXT,
    audio_url TEXT,
    FOREIGN KEY(lesson_id) REFERENCES lessons(id) ON DELETE CASCADE
);
```

### 2.5 Bảng `quizzes` (Bộ Câu hỏi Trắc nghiệm)
```sql
CREATE TABLE IF NOT EXISTS quizzes (
    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    lesson_id TEXT NOT NULL,
    question TEXT NOT NULL,
    options_json TEXT NOT NULL, -- JSON Array: ["Apple", "Banana", "Orange", "Grape"]
    correct_answer TEXT NOT NULL,
    explanation TEXT,
    quiz_type TEXT NOT NULL DEFAULT 'MULTIPLE_CHOICE' -- 'MULTIPLE_CHOICE' | 'DRAG_DROP'
);
```

### 2.6 Bảng `user_progress` (Lưu Tiến độ Học bài)
```sql
CREATE TABLE IF NOT EXISTS user_progress (
    lesson_id TEXT PRIMARY KEY NOT NULL,
    is_completed INTEGER NOT NULL DEFAULT 0,
    completion_percentage INTEGER NOT NULL DEFAULT 0,
    last_studied_at INTEGER NOT NULL,
    stars_earned INTEGER NOT NULL DEFAULT 0
);
```

### 2.7 Bảng `srs_cards` (Lưu Trạng thái Lật Flashcard SRS)
```sql
CREATE TABLE IF NOT EXISTS srs_cards (
    vocab_id INTEGER PRIMARY KEY NOT NULL,
    interval_days INTEGER NOT NULL DEFAULT 1,
    repetition_count INTEGER NOT NULL DEFAULT 0,
    ease_factor REAL NOT NULL DEFAULT 2.5,
    next_review_at INTEGER NOT NULL,
    memory_rating TEXT NOT NULL DEFAULT 'NEW', -- 'AGAIN' | 'HARD' | 'GOOD' | 'EASY'
    FOREIGN KEY(vocab_id) REFERENCES vocabulary(id) ON DELETE CASCADE
);
```

---

## 🛠️ 3. ĐỊNH NGHĨA ROOM ENTITIES KOTLIN

### 3.1 `TopicEntity.kt`
```kotlin
package com.speakhub.advmobile.template.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "topics")
data class TopicEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "app_id") val appId: String,
    val title: String,
    val icon: String,
    val description: String?,
    val level: String,
    @ColumnInfo(name = "display_order") val displayOrder: Int
)
```

### 3.2 `VocabularyEntity.kt`
```kotlin
package com.speakhub.advmobile.template.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "vocabulary",
    foreignKeys = [
        ForeignKey(
            entity = LessonEntity::class,
            parentColumns = ["id"],
            childColumns = ["lesson_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class VocabularyEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "lesson_id") val lessonId: String,
    val word: String,
    val ipa: String,
    val meaning: String,
    @ColumnInfo(name = "example_en") val exampleEn: String?,
    @ColumnInfo(name = "example_native") val exampleNative: String?,
    @ColumnInfo(name = "part_of_speech") val partOfSpeech: String?,
    @ColumnInfo(name = "audio_url") val audioUrl: String?
)
```

---

## 🏛️ 4. KHỞI TẠO ROOM ASSET DATABASE & HILT MODULE

### 4.1 `AppDatabase.kt`
```kotlin
package com.speakhub.advmobile.template.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.speakhub.advmobile.template.data.local.dao.*
import com.speakhub.advmobile.template.data.local.entity.*

@Database(
    entities = [
        TopicEntity::class,
        LessonEntity::class,
        ConversationEntity::class,
        VocabularyEntity::class,
        QuizEntity::class,
        UserProgressEntity::class,
        SrsCardEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun topicDao(): TopicDao
    abstract fun lessonDao(): LessonDao
    abstract fun flashcardDao(): FlashcardDao
    abstract fun progressDao(): ProgressDao
}
```

### 4.2 Hilt Provider (`DatabaseModule.kt`)
```kotlin
package com.speakhub.advmobile.template.di

import android.content.Context
import androidx.room.Room
import com.speakhub.advmobile.template.config.AppConfig
import com.speakhub.advmobile.template.data.local.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "advmobile_${AppConfig.APP_ID}.db"
        )
        .createFromAsset(AppConfig.DB_ASSET_PATH) // 🚀 TỰ ĐỘNG LOAD FILE SQLITE TỪ ASSETS
        .fallbackToDestructiveMigration()
        .build()
    }

    @Provides
    fun provideTopicDao(db: AppDatabase) = db.topicDao()

    @Provides
    fun provideLessonDao(db: AppDatabase) = db.lessonDao()

    @Provides
    fun provideProgressDao(db: AppDatabase) = db.progressDao()
}
```

---

## ⚡ 5. ROOM DAOS VỚI KOTLIN COROUTINES FLOW

### 5.1 `LessonDao.kt`
```kotlin
package com.speakhub.advmobile.template.data.local.dao

import androidx.room.*
import com.speakhub.advmobile.template.data.local.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface LessonDao {
    @Query("SELECT * FROM lessons WHERE topic_id = :topicId ORDER BY display_order ASC")
    fun getLessonsByTopic(topicId: String): Flow<List<LessonEntity>>

    @Query("SELECT * FROM conversations WHERE lesson_id = :lessonId ORDER BY display_order ASC")
    fun getConversationsByLesson(lessonId: String): Flow<List<ConversationEntity>>

    @Query("SELECT * FROM vocabulary WHERE lesson_id = :lessonId")
    fun getVocabularyByLesson(lessonId: String): Flow<List<VocabularyEntity>>

    @Query("SELECT * FROM quizzes WHERE lesson_id = :lessonId")
    fun getQuizzesByLesson(lessonId: String): Flow<List<QuizEntity>>
}
```

### 5.2 Algorithm SRS Spaced Repetition (SuperMemo-2) Trong ProgressDao

```kotlin
// Hàm tính toán khoảng thời gian lặp lại theo thuật toán SRS SuperMemo-2
fun calculateNextSrsReview(
    currentInterval: Int,
    currentRepetition: Int,
    currentEaseFactor: Float,
    rating: SrsRating // AGAIN (1), HARD (2), GOOD (3), EASY (4)
): SrsResult {
    var interval = currentInterval
    var repetition = currentRepetition
    var easeFactor = currentEaseFactor

    val q = when (rating) {
        SrsRating.AGAIN -> 1
        SrsRating.HARD -> 2
        SrsRating.GOOD -> 4
        SrsRating.EASY -> 5
    }

    if (q >= 3) {
        repetition += 1
        interval = when (repetition) {
            1 -> 1
            2 -> 6
            else -> (interval * easeFactor).toInt()
        }
    } else {
        repetition = 0
        interval = 1
    }

    easeFactor += (0.1f - (5 - q) * (0.08f + (5 - q) * 0.02f))
    if (easeFactor < 1.3f) easeFactor = 1.3f

    return SrsResult(interval, repetition, easeFactor)
}
```
