# ⚡ CLONE & CONFIGURATION GUIDE FOR ADV-MOBILE APPS

> **Dành cho:** AI Coding Agent (Copilot / Gemini / Claude) & Developers  
> **Mục tiêu:** Hướng dẫn quy trình 4 bước clone và cấu hình dự án mới thuộc nhóm AdvMobile trong 5 phút mà không cần viết lại core engine hay database logic.

---

## 📋 QUY TRÌNH 4 BƯỚC CLONE MỘT ỨNG DỤNG ADVMOBILE MỚI

### 🔹 BƯỚC 1: Nhân Bản Thư Mục Base Project (Clone Base Structure)
1. Copy toàn bộ thư mục mã nguồn Base App (`com.speakhub.advmobile.template`).
2. Đổi tên package phù hợp với app mới (Ví dụ: `com.speakhub.advmobile.business` cho ứng dụng Learn English Business).
3. Đổi tên file ứng dụng trong `settings.gradle.kts` và `app/build.gradle.kts`.

---

### 🔹 BƯỚC 2: Cập Nhật File Cấu Hình Duy Nhất (`AppConfig.kt`)
Mở file `app/src/main/java/com/speakhub/advmobile/template/config/AppConfig.kt` và thay đổi các thông số cấu hình tương ứng với app mới theo bảng dưới đây:

#### 📊 Bảng Cấu Hình Tham Chiếu Các App AdvMobile:

| App ID | Tên App (`APP_NAME`) | DB Asset Name (`DB_ASSET_PATH`) | Primary Color (`Colors.Primary`) | Accent Color (`Colors.Secondary`) |
| :--- | :--- | :--- | :--- | :--- |
| `en-basic` | Learn English Basic | `database/en_basic.db` | `#10B981` (Emerald) | `#F59E0B` (Amber) |
| `en-business` | Learn English Business | `database/en_business.db` | `#1E293B` (Midnight Navy) | `#D97706` (Executive Gold) |
| `en-travel` | Learn English Travel | `database/en_travel.db` | `#0284C7` (Sky Blue) | `#F43F5E` (Coral Pink) |
| `en-interview` | Learn English Interview | `database/en_interview.db` | `#4F46E5` (Royal Indigo) | `#06B6D4` (Electric Cyan) |
| `en-pronunciation`| Learn English Pronunciation | `database/en_pronunciation.db` | `#E91E63` (Rose Pink) | `#8B5CF6` (Soft Violet) |
| `en-grammar` | Learn English Grammar | `database/en_grammar.db` | `#F59E0B` (Solar Amber) | `#0891B2` (Deep Cyan) |
| `en-regular` | Learn English Regular | `database/en_regular.db` | `#F43F5E` (Soft Rose) | `#0D9488` (Coral Teal) |
| `en-phrases-idioms`| Learn Phrases & Idioms | `database/en_idioms.db` | `#7C3AED` (Deep Violet) | `#84CC16` (Bright Lime) |

---

### 🔹 BƯỚC 3: Thay Thể File SQLite Chuẩn Bị Sẵn
1. Copy file `.db` đã chuẩn bị đúng cấu trúc SQLite (chứa dữ liệu `topics`, `lessons`, `vocabulary`, `conversations`, `quizzes`) vào thư mục:
   `app/src/main/assets/database/`
2. Đảm bảo tên file trùng khớp với biến `DB_ASSET_PATH` đã khai báo trong `AppConfig.kt`.

---

### 🔹 BƯỚC 4: Kiểm Tra Giao Diện UI & Khởi Chạy
1. Chạy lệnh Build APK / Run App trên Android Emulator hoặc thiết bị thật.
2. Room database sẽ tự động load toàn bộ bài học từ file `.db` trong assets.
3. Toàn bộ chủ đề màu sắc Material 3 và icon sẽ tự động chuyển đổi theo `AppConfig.kt`.

---

## 🤖 BỘ PROMPT CHUẨN DÀNH CHO AI AGENT KHI GENERATE CODE COMPOSE

Khi yêu cầu AI sinh mã nguồn Compose cho một ứng dụng AdvMobile cụ thể, hãy sử dụng mẫu Prompt dưới đây:

```text
You are an expert Android Developer building a native app using Kotlin, Jetpack Compose, Material 3, and Clean Architecture.

Target App: Learn English Business (App ID: en-business)
UI Specification Reference: Please read design/mobile-ui/advmobile/learn_english_business_ui.md
Technical Architecture Reference: Please follow guidelines in design/mobile-ui/advmobile/tech/ANDROID_ARCHITECTURE_SPEC.md and SQLITE_SCHEMA_AND_ROOM_SPEC.md

Instructions:
1. Update `AppConfig.kt` to use Midnight Navy (#1E293B) as primary color and Gold (#D97706) as accent color. Database asset path is "database/en_business.db".
2. Implement `HomeScreen.kt` using Jetpack Compose with Executive Track cards, Executive streak widget, and 5 Skill Tabs engine integration.
3. Ensure Room database loads pre-packaged content via `Room.databaseBuilder(...).createFromAsset("database/en_business.db")`.
4. Implement the 5 Skill Engine composables: DialogueChatEngine, SrsFlashcardEngine (with 3D flip animation), ListeningEngine, SpeakingEngine (SpeechRecognizer), and WordAssemblyQuizEngine.
```
