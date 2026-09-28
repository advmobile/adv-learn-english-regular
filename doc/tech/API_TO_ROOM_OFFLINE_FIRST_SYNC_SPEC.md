# API → Room SQLite Offline-First Content Sync Specification

> **Dành cho:** Android AI coding agent và Android developers  
> **Phạm vi:** Tất cả app nội dung của SpeakHub: AdvMobile lesson-based (Basic, Business, Grammar, Travel, …) và FPMI bilingual conversation-based (Vietnamese, French, Japanese, …)  
> **Thay thế:** Cách tải file SQLite `.db` từ CDN hoặc đóng gói content database trong APK  
> **Nguồn content:** SpeakHub Web API → SQLite/Room trên thiết bị  
> **Mục tiêu:** Chỉ tải nội dung khi local database chưa có; sau đó app chạy offline từ Room, không tải lại nội dung bất biến ở mỗi lần mở app.

---

## 1. Quyết định kiến trúc

Không phân phối file SQLite qua CDN và không dùng `Room.createFromAsset(...)` cho content khóa học.

Thay vào đó, lần đầu ứng dụng chạy:

1. App gọi SpeakHub API.
2. App tải toàn bộ content thuộc `APP_ID` đang được build, theo `APP_TYPE`.
3. App chuẩn hoá các DTO API và ghi vào SQLite thông qua Room trong transaction.
4. App đọc content **chỉ từ Room** để hiển thị UI, làm quiz, SRS và học offline.
5. Những lần mở sau: nếu local content đã hoàn chỉnh, đọc Room ngay; không gọi API content lại.

```text
┌────────────────┐       first install / explicit refresh      ┌─────────────────────┐
│ Android app    │ ───────────────────────────────────────────► │ SpeakHub Web API    │
│ Retrofit/OkHttp│                                                │ api.speakhub.org    │
└───────┬────────┘                                                └─────────┬───────────┘
        │ DTOs                                                               │
        ▼                                                                    │
┌───────────────────────────────────────────────────────────────────────────┘
│ ContentSyncRepository
│  validate → map → Room.withTransaction { replace content atomically }
▼
┌───────────────────────────────────────────────────────────────────────────┐
│ SQLite / Room                                                              │
│   content: topics, lessons, phrases, conversations, vocabulary, quizzes   │
│   user state: user_progress, srs_cards, user_stats                         │
└───────────────────────────────┬───────────────────────────────────────────┘
                                │ always read for normal learning
                                ▼
                         Jetpack Compose UI
                         (works offline)
```

### Lý do

- Không có file SQLite lớn phải host, tải, giải nén và kiểm tra từ CDN.
- API là nguồn dữ liệu thống nhất mà web đang sử dụng.
- Android lấy đúng nội dung theo app và locale; không cần build lại APK khi content mới được công bố.
- Room vẫn cho tốc độ đọc local, tìm kiếm, offline learning và persistence cho SRS/progress.
- Có thể retry request theo từng endpoint; một file `.db` hỏng không làm mất toàn bộ lần tải.

> **Nguyên tắc:** API là nguồn chuẩn cho **course content**. SQLite trên máy là cache/persistent local read model. Dữ liệu người học vẫn là dữ liệu local riêng, không được xoá khi refresh content.

---

## 2. Một API dùng chung cho tất cả app

Backend đã có API tổng quát theo mẫu `/api/apps/{appId}/...`; **không tạo API client hay endpoint riêng cho từng app clone**. Khi tạo Android app mới, chỉ thay `APP_ID`, `APP_TYPE` và `CONTENT_LANGUAGE` trong cấu hình. API sẽ nhận diện app đó là `adv` hoặc `fpmi`.

| Loại app | Ví dụ | Content cần lưu vào Room |
|---|---|---|
| `adv` | `learn-english-basic`, `learn-english-business`, `learn-english-grammar` | topics → lessons → lesson phrases/content |
| `fpmi` | `vi-english-conversation`, `french-english-conversation` | categories/topics → phrases/words, conversations → conversation lines |

`GET /api/apps` trả về tất cả app, chia theo `adv` và `fpmi`. `GET /api/apps/{appId}` trả về metadata và `type`; Android có thể dùng endpoint này để xác minh AppConfig trong debug/onboarding, nhưng không cần gọi ở mọi lần mở app sau khi Room đã `COMPLETE`.

## 3. Cấu hình bắt buộc theo từng app

Mỗi app clone phải có một `APP_ID` cố định, đúng `id` trên SpeakHub API. Không suy đoán ID từ app name hoặc Android package name.

```kotlin
object AppConfig {
    // Ví dụ. Giá trị thật phải khớp cột adv_apps.id của backend.
    const val APP_ID = "learn-english-basic"
    const val APP_TYPE = "adv" // "adv" | "fpmi"

    // Native translation dùng để hiển thị phrase/lesson trên Android.
    const val CONTENT_LANGUAGE = "vi"

    const val API_BASE_URL = "https://api.speakhub.org/"
    const val API_SCHEMA_VERSION = 1
}
```

`CONTENT_LANGUAGE` chỉ là ngôn ngữ bản dịch, **không phải** ngôn ngữ của UI Android. UI có thể dùng Android resources theo nhu cầu.

### URL API hiện dùng

Base URL:

```text
https://api.speakhub.org/api/
```

Với `appId = learn-english-basic`, `lang = vi`:

| Mục đích | Endpoint | Loại app |
|---|---|
| Liệt kê/cấu hình app | `GET /api/apps`, `GET /api/apps/{appId}` | tất cả |
| Danh sách topic | `GET /api/apps/{appId}/topics?lang={lang}` | `adv`; với FPMI API này tương thích và trả categories |
| Topic và danh sách lesson | `GET /api/apps/{appId}/topics/{topicId}?lang={lang}` | `adv` |
| Bài học và phrases | `GET /api/apps/{appId}/topics/{topicId}/lessons/{lessonId}?lang={lang}` | `adv` |
| Categories | `GET /api/apps/{appId}/categories?lang={lang}` | `fpmi` |
| Category detail + phrases/words | `GET /api/apps/{appId}/categories/{categoryId}?lang={lang}` | `fpmi` |
| Conversations | `GET /api/apps/{appId}/conversations?lang={lang}` | `fpmi` |
| Conversation + lines | `GET /api/apps/{appId}/conversations/{conversationId}?lang={lang}` | `fpmi` |
| All phrases | `GET /api/apps/{appId}/phrases?lang={lang}` | `fpmi` |

Không dùng các URL web như `/vi/apps/...` cho Retrofit; đây là route UI/SEO. Android chỉ gọi host API.

---

## 4. Vòng đời tải dữ liệu

### 4.1 Lần mở đầu tiên

`BootstrapContentUseCase` chạy trong splash/onboarding ViewModel:

1. Đọc `content_sync_state` theo `appId + contentLanguage`.
2. Nếu chưa có state `COMPLETE`, hiển thị progress tải nội dung.
3. Kiểm tra mạng; nếu offline và Room không có data hoàn chỉnh, hiển thị màn hình “Cần kết nối Internet lần đầu để tải khóa học”, có nút Retry.
4. Gọi API theo `APP_TYPE`:
   - `adv`: app → topics → topic detail → lesson detail.
   - `fpmi`: app → categories → category details, conversations → conversation details; có thể gọi `/phrases` khi cần bulk phrase cache.
5. Giữ dữ liệu tạm trong memory hoặc staging tables; **chưa đánh dấu complete**.
6. Ghi toàn bộ content trong một Room transaction.
7. Ghi `content_sync_state = COMPLETE`; sau đó điều hướng vào Home.

Khi lần tải bị fail, không được đánh dấu content là complete. Lần sau app mở hoặc user bấm Retry sẽ tiếp tục sync lại từ đầu. Vì content khóa học không quá lớn và bất biến, cách đơn giản này an toàn hơn resume từng item trong phiên bản đầu.

### 4.2 Những lần mở sau

Nếu Room có state `COMPLETE` và content schema/version hợp lệ:

- Render Home từ Room ngay cả khi không có Internet.
- Không gọi endpoint content trong `HomeViewModel`, `TopicViewModel`, `LessonViewModel`.
- Không hiển thị loading network mỗi lần người học vào lesson.

### 4.3 Refresh chủ động và thay đổi nội dung trong tương lai

Hiện giả định của sản phẩm là content API ổn định, do đó không có auto-refresh mỗi lần mở app.

Tuy nhiên phải thiết kế sẵn đường cập nhật:

- Settings có nút **Check for content update** / **Refresh downloaded lessons**.
- API sau này nên trả về `content_version` hoặc `updated_at` ở endpoint app/manifest.
- Chỉ đồng bộ lại khi server version khác `content_sync_state.contentVersion`.
- Khi thay đổi schema Room, dùng Room migration; không xoá progress/SRS.

Không dùng TTL kiểu “7 ngày thì tải lại” cho content. Điều đó đi ngược mục tiêu chỉ download một lần, làm tốn data và khiến offline behavior khó đoán.

---

## 5. Room schema bổ sung

Giữ các content/user tables trong `SQLITE_SCHEMA_AND_ROOM_SPEC.md`, nhưng thêm bảng metadata dưới đây. Đây là bảng bắt buộc để phân biệt “đã có vài row” với “đã tải hoàn chỉnh”.

```sql
CREATE TABLE IF NOT EXISTS content_sync_state (
    app_id TEXT NOT NULL,
    content_language TEXT NOT NULL,
    status TEXT NOT NULL, -- DOWNLOADING | COMPLETE | FAILED
    content_version TEXT,
    schema_version INTEGER NOT NULL,
    synced_at INTEGER,
    last_error TEXT,
    PRIMARY KEY (app_id, content_language)
);
```

Tất cả bảng **content** nên có `app_id` và `content_language` nếu template có khả năng hỗ trợ nhiều app/locale trong cùng database. Nếu mỗi APK chỉ chứa một app và một locale thì vẫn nên giữ `app_id`, giúp debug và migration rõ ràng hơn.

```kotlin
@Entity(
    tableName = "content_sync_state",
    primaryKeys = ["app_id", "content_language"]
)
data class ContentSyncStateEntity(
    @ColumnInfo(name = "app_id") val appId: String,
    @ColumnInfo(name = "content_language") val contentLanguage: String,
    val status: ContentSyncStatus,
    @ColumnInfo(name = "content_version") val contentVersion: String?,
    @ColumnInfo(name = "schema_version") val schemaVersion: Int,
    @ColumnInfo(name = "synced_at") val syncedAt: Long?,
    @ColumnInfo(name = "last_error") val lastError: String?
)

enum class ContentSyncStatus { DOWNLOADING, COMPLETE, FAILED }
```

### Tách content và user state

| Nhóm | Ví dụ | Khi refresh content |
|---|---|---|
| Course content | topics, lessons, phrases, quizzes, audio URL | Có thể replace theo `app_id` trong transaction |
| User state | user_progress, srs_cards, user_stats, settings | Tuyệt đối không xoá |
| Metadata | content_sync_state | Cập nhật sau khi transaction content thành công |

Nếu phrase/lesson IDs của backend là ID ổn định, `srs_cards` có thể giữ trực tiếp `phrase_id`. Nếu backend có thể thay ID, cần stable content key trước khi triển khai refresh version.

---

## 6. Retrofit contract

```kotlin
interface SpeakHubApi {
    @GET("api/apps/{appId}")
    suspend fun getApp(@Path("appId") appId: String): AppDto

    @GET("api/apps/{appId}/topics")
    suspend fun getTopics(
        @Path("appId") appId: String,
        @Query("lang") language: String
    ): List<TopicDto>

    @GET("api/apps/{appId}/topics/{topicId}")
    suspend fun getTopic(
        @Path("appId") appId: String,
        @Path("topicId") topicId: Long,
        @Query("lang") language: String
    ): TopicDetailDto

    @GET("api/apps/{appId}/topics/{topicId}/lessons/{lessonId}")
    suspend fun getLesson(
        @Path("appId") appId: String,
        @Path("topicId") topicId: Long,
        @Path("lessonId") lessonId: Long,
        @Query("lang") language: String
    ): LessonDetailDto
}
```

FPMI bổ sung contract sau (dùng cùng `SpeakHubApi`, không tạo `FrenchApi`, `VietnameseApi`, …):

```kotlin
@GET("api/apps/{appId}/categories")
suspend fun getCategories(@Path("appId") appId: String, @Query("lang") language: String): List<CategoryDto>

@GET("api/apps/{appId}/categories/{categoryId}")
suspend fun getCategory(@Path("appId") appId: String, @Path("categoryId") categoryId: Long, @Query("lang") language: String): CategoryDetailDto

@GET("api/apps/{appId}/conversations")
suspend fun getConversations(@Path("appId") appId: String, @Query("lang") language: String): List<ConversationDto>

@GET("api/apps/{appId}/conversations/{conversationId}")
suspend fun getConversation(@Path("appId") appId: String, @Path("conversationId") conversationId: Long, @Query("lang") language: String): ConversationDetailDto

@GET("api/apps/{appId}/phrases")
suspend fun getPhrases(@Path("appId") appId: String, @Query("lang") language: String): List<PhraseDto>
```

Thiết lập OkHttp:

- `connectTimeout`: 15 giây.
- `readTimeout`: 30 giây.
- Retry có giới hạn (ví dụ 3 lần với exponential backoff) cho `IOException` và HTTP 5xx.
- Không retry HTTP 4xx ngoại trừ trường hợp token/configuration được cập nhật.
- Không log body lesson/translation đầy đủ ở release build.

API hiện read-only nên Android không cần API key write. Không đưa Supabase service role key vào APK.

---

## 7. Thuật toán sync an toàn

```kotlin
class ContentSyncRepository @Inject constructor(
    private val api: SpeakHubApi,
    private val database: AppDatabase,
    @IoDispatcher private val io: CoroutineDispatcher
) {
    suspend fun downloadInitialContent(config: AppConfig): Result<Unit> = withContext(io) {
        runCatching {
            database.syncStateDao().upsertDownloading(
                appId = config.APP_ID,
                language = config.CONTENT_LANGUAGE,
                schemaVersion = config.API_SCHEMA_VERSION
            )

            require(config.APP_TYPE == "adv") { "Use syncFpmiContent for FPMI apps" }
            val topics = api.getTopics(config.APP_ID, config.CONTENT_LANGUAGE)
            val completeTopics = topics.map { topic ->
                api.getTopic(config.APP_ID, topic.id, config.CONTENT_LANGUAGE)
            }
            val completeLessons = completeTopics.flatMap { topic ->
                topic.lessons.map { lesson ->
                    api.getLesson(config.APP_ID, topic.id, lesson.id, config.CONTENT_LANGUAGE)
                }
            }

            // Validate before any destructive local content change.
            require(topics.isNotEmpty()) { "API returned no topics" }
            require(completeLessons.isNotEmpty()) { "API returned no lessons" }

            database.withTransaction {
                // Delete only rows owned by this app + content locale.
                // Never delete user_progress, srs_cards, user_stats, settings.
                database.contentDao().deleteContent(config.APP_ID, config.CONTENT_LANGUAGE)
                database.contentDao().insertTopics(mapTopics(topics, config))
                database.contentDao().insertLessons(mapLessons(completeTopics, config))
                database.contentDao().insertPhrases(mapPhrases(completeLessons, config))
                database.syncStateDao().markComplete(
                    appId = config.APP_ID,
                    language = config.CONTENT_LANGUAGE,
                    schemaVersion = config.API_SCHEMA_VERSION,
                    syncedAt = System.currentTimeMillis()
                )
            }
        }.onFailure { error ->
            database.syncStateDao().markFailed(
                appId = config.APP_ID,
                language = config.CONTENT_LANGUAGE,
                error = error.message ?: error.javaClass.simpleName
            )
        }
    }
}
```

`syncFpmiContent()` dùng cùng nguyên tắc transaction/state, nhưng map `CategoryDto`, `PhraseDto`, `ConversationDto`, `ConversationLineDto` vào các bảng content FPMI. Không dùng DAO hay schema tên riêng theo từng ngôn ngữ; tất cả query có điều kiện `app_id + content_language`.

### Các điều kiện không được vi phạm

1. Không xoá local content cũ trước khi tất cả API response mới đã tải và validate thành công.
2. Mọi delete/insert content/state `COMPLETE` phải nằm trong cùng `withTransaction`.
3. `COMPLETE` chỉ được set sau insert thành công.
4. UI không đọc một database đang half-sync; `Flow` từ Room chỉ phát trạng thái mới sau transaction.
5. Không bao giờ xoá user progress/SRS trong `deleteContent`.

---

## 8. Repository và UI contract

Presentation layer không được gọi Retrofit trực tiếp. UI luôn quan sát Room.

```kotlin
interface ContentRepository {
    fun observeTopics(appId: String, language: String): Flow<List<Topic>>
    fun observeTopic(topicId: Long): Flow<TopicWithLessons>
    fun observeLesson(lessonId: Long): Flow<LessonWithPhrases>
    suspend fun ensureInitialContent(): SyncResult
    suspend fun refreshContentIfRequested(): SyncResult
}
```

```text
SplashViewModel
  ├─ Room COMPLETE? ─ yes → Home immediately
  └─ no → DownloadContentScreen → ContentSyncRepository → Room COMPLETE → Home

HomeViewModel / TopicViewModel / LessonViewModel
  └─ Room DAO Flow only; no network request
```

`DownloadContentScreen` nên hiển thị progress thật:

- `Fetching course information`
- `Downloading topic 3/12`
- `Saving lessons for offline learning`
- `Ready to learn offline`

Không báo “100% complete” trước khi Room transaction hoàn tất.

---

## 9. Background work, failures và UX

### First download

- Dùng ViewModel coroutine khi user đang chờ onboarding; không cần WorkManager nếu app phải chờ content để hoạt động.
- Nếu app bị background/kill, state là `DOWNLOADING` hoặc `FAILED`; lần mở tiếp theo bắt đầu lại sync an toàn.

### Manual refresh / future version update

- Dùng `WorkManager` với `Constraints(NetworkType.CONNECTED)`.
- Tên unique work: `content-sync-{APP_ID}-{CONTENT_LANGUAGE}`.
- Dùng `ExistingWorkPolicy.KEEP` để tránh hai transaction sync chạy song song.
- Với refresh lớn, thông báo rõ “Content will be available offline after update”.

### Offline UX

| Room state | Network | Hành vi |
|---|---|---|
| COMPLETE | bất kỳ | Vào app ngay, đọc SQLite |
| Chưa có content | online | Tải API và lưu SQLite |
| Chưa có content | offline | Màn hình retry/connect Internet; không crash, không home rỗng |
| FAILED | online | Cho Retry |
| FAILED nhưng còn content COMPLETE cũ | bất kỳ | Dùng content cũ; báo refresh chưa thành công trong Settings |

---

## 10. Audio và tài nguyên không phải JSON

SQLite chỉ lưu `audio_url`, text, metadata và trạng thái cache. Không nhét MP3 vào SQLite.

- Dùng Media3/ExoPlayer phát `audio_url`.
- Dùng `SimpleCache`/`CacheDataSource` của Media3 để cache audio đã nghe.
- Cache audio có quota (ví dụ 150 MB), eviction LRU.
- Không để audio failure làm transaction content bị fail; người học vẫn đọc text offline.
- Nếu cần offline audio 100%, cung cấp thao tác “Download audio for this topic” riêng, kèm dung lượng dự kiến.

---

## 11. Kiểm thử bắt buộc

AI/Android developer phải tạo test cho các case sau:

1. Fresh install, online: API được gọi, Room có đủ topic/lesson/phrase, state = `COMPLETE`.
2. Mở lại sau first sync, offline: app vào Home và lesson đọc từ Room; Retrofit không được gọi.
3. Mất mạng giữa lúc tải lesson: state không `COMPLETE`; không xuất hiện course half-data ở Home.
4. Retry sau failure: sync thành công và không duplicate rows.
5. Manual refresh: user progress/SRS trước và sau refresh không thay đổi.
6. API trả list rỗng/corrupt: validation fail, giữ local content COMPLETE trước đó.
7. Hai refresh request đồng thời: chỉ có một worker/transaction chạy.
8. Đổi `CONTENT_LANGUAGE`: nhận diện là content cache khác; không overwrite content của locale cũ.
9. Với cả một `adv` app và một `fpmi` app: kiểm tra đúng endpoint set và đúng tables được populate.

---

## 12. Checklist triển khai cho AI Android

- [ ] Bỏ `Room.createFromAsset()` và mọi logic download `.db` từ CDN cho course content.
- [ ] Thêm Retrofit/OkHttp API client trỏ `https://api.speakhub.org/api/`.
- [ ] Khai báo `AppConfig.APP_ID` chính xác cho app đang clone.
- [ ] Khai báo `AppConfig.APP_TYPE` là `adv` hoặc `fpmi`; dùng một Retrofit service chung.
- [ ] Thêm `content_sync_state` entity, DAO và migration Room.
- [ ] Tách DAO/content tables khỏi progress/SRS tables.
- [ ] Viết `ContentSyncRepository` và `BootstrapContentUseCase`.
- [ ] Ghi content atomically trong `RoomDatabase.withTransaction`.
- [ ] Chỉ đọc Room trong các ViewModel học tập.
- [ ] Làm Download/Retry/Offline first-run screens.
- [ ] Thêm Settings manual refresh (có thể feature-flag đến khi API có `content_version`).
- [ ] Viết test offline-first ở mục 10.

---

## 13. Các tài liệu liên quan

- `ANDROID_ARCHITECTURE_SPEC.md`: thay phần “Room Pre-Packaged Asset Database” bằng “Room Offline Content Cache”.
- `SQLITE_SCHEMA_AND_ROOM_SPEC.md`: giữ các entity/content tables và user-state tables, bổ sung `content_sync_state` ở tài liệu này.
- `SKILL_ENGINES_COMPOSE_SPEC.md`: các skill engines lấy phrases/lessons từ repository/Room, không phụ thuộc network.
- `help-api.md` ở root repository: danh sách endpoint và cấu trúc API hiện tại.

> Khi API bổ sung endpoint manifest/version hoặc bulk content endpoint, cập nhật tài liệu này trước; không quay lại cơ chế tải file SQLite qua CDN.
