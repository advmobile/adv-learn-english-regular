# 🎨 LEARN ENGLISH BASIC - BẢN ĐẶC TẢ GIAO DIỆN MOBILE UI & PROMPT CHO GOOGLE STITCH

> **App ID:** `en-basic` / `learn-english-basic`  
> **Nhóm:** AdvMobile Series  
> **Loại ứng dụng:** Mobile App (iOS / Android)  
> **Mô tả:** Ứng dụng di động học tiếng Anh nền tảng dành cho người mới bắt đầu (Beginner A1-A2), tập trung vào từ vựng cơ bản, mẫu câu giao tiếp đơn giản và ngữ pháp nhập môn.  
> **Tương thích:** Tối ưu hóa để import/upload trực tiếp vào **Google Stitch** (`https://stitch.withgoogle.com/`).

---

## 📐 1. THÔNG TIN CHUNG & PHONG CÁCH THIẾT KẾ MOBILE (DESIGN SYSTEM)

### 1.1 Palette Màu Sắc Mobile (Color Tokens)
- **Primary Color (Chủ đạo):** Emerald Green (`#10B981`, Gradient: `from-emerald-500 to-teal-700`) - Mang lại cảm giác khởi đầu tươi trẻ, tự tin và thân thiện.
- **Secondary / Accent:** Warm Amber / Solar Gold (`#F59E0B`, `#FFB300`) cho chỉ số Streak, điểm XP và Huy hiệu thành tích.
- **Backgrounds:**
  - Light Theme: Mint Clean (`#F0FDF4`, Surface: `#FFFFFF`, Card: `#FFFFFF`)
  - Dark Theme: Deep Forest Glass (`#064E3B`, `#022C22`, Card: `rgba(6, 78, 59, 0.6)`)
- **Typography Mobile:** `Plus Jakarta Sans` cho Tiêu đề (Bold 800), `Inter` cho Nội dung bài học và Monospace cho Phiên âm IPA.

### 1.2 Thành Phần Giao Diện Di Động Đắc Thụ (Mobile Components)
- **Header Dock:** Sticky Navigation phía trên hiển thị Avatar, Streak 🔥, XP Badge và Nút Ngôn ngữ gốc.
- **Bottom Navigation Dock:** Floating Bar 4 Tabs (`Home`, `Learn`, `Flashcard`, `Profile`) bo tròn `rounded-full backdrop-blur-xl`.
- **Cards & Buttons:** Bo góc `24px`, hiệu ứng shadow nổi nhẹ, phản hồi chạm micro-haptic.

---

## 📱 2. CHI TIẾT GIAO DIỆN MÀN HÌNH (SCREEN-BY-SCREEN LAYOUT)

### MÀN HÌNH 1: SPLASH & ONBOARDING (Chào mừng & Thiết lập mục tiêu)
- **Header:** Logo icon mầm cây vươn lên kèm tên app "Learn English Basic".
- **Hero Graphic:** Minh họa 3D phong cách hoạt hình hiện đại về việc bắt đầu học tiếng Anh.
- **Option Picker:** Chọn thời gian học hàng ngày (5 phút, 10 phút, 15 phút, 30 phút).
- **CTA Button:** Nút viên thuốc kích thước lớn `"Bắt Đầu Học Ngay"` màu Gradient Emerald Green.

### MÀN HÌNH 2: DASHBOARD CHÍNH (Home & Curriculum Path)
- **Top Bar:** 
  - Đếm chuỗi ngày học 🔥 `7 Ngày Continuous`
  - Thanh điểm XP `350 / 500 XP`
  - Icon Nút cài đặt & Thông báo.
- **Section "Đang Học":** Card bài học tiếp theo với nút `"Tiếp tục Bài 03: Numbers & Colors"` (Progress bar 60%).
- **Lưới Cây Tiến Độ (Learning Tree / Path):**
  - Cụm 1: *Foundations* (Tự giới thiệu, Số đếm & Màu sắc, Gia đình)
  - Cụm 2: *Daily Routines* (Thức dậy, Đồ ăn & Thức uống, Thời gian)
  - Cụm 3: *Basic Grammar* (Động từ To Be, Thì Hiện tại đơn cơ bản)
  - Mỗi node bài học có hình tròn bo góc lớn, icon minh họa, ngôi sao hoàn thành ⭐️⭐️⭐️.

### MÀN HÌNH 3: TRANG BÀI HỌC VỚI 5 SKILL MODES MOBILE (Multi-Skill Mobile Engine)

#### 📖 1. Learn Mode (Bài giảng & Từ vựng căn bản):
- Card bài viết ngắn gọn, chữ to dễ đọc trên mobile.
- Danh sách từ vựng nền tảng kèm hình ảnh minh họa sống động.
- Phát âm từng từ bằng giọng đọc chuẩn Anh - Mỹ (Nút loa to).

#### 🎴 2. Practice Mode (Flashcard 3D Lật Mặt):
- Thẻ Flashcard to giữa màn hình mobile.
- Lật 3D khi chạm: Mặt trước (Từ tiếng Anh + Phiên âm IPA + Loa) -> Mặt sau (Nghĩa Việt + Hình ảnh + Ví dụ).
- 4 Nút đánh giá dưới cùng: 🔴 *Lặp lại*, 🟡 *Khó*, 🟢 *Đã nhớ*, 🔵 *Rất dễ*.

#### 🎧 3. Listening Mode (Nghe điền từ đơn giản):
- Player dạng sóng âm màu xanh Emerald.
- Nút giảm tốc độ đọc `0.75x` để nghe rõ từng phát âm.
- Câu thiếu từ với các ô trống gợi ý chữ cái đầu.

#### 🎙️ 4. Speaking Mode (Luyện nói từ vựng & Câu ngắn):
- Microphone button lớn phát sáng giữa màn hình.
- Phản hồi điểm phát âm dạng vòng tròn `0 - 100%`.
- Tô màu xanh lục lá cây cho các từ phát âm đúng.

#### 🧪 5. Quiz Mode (Trắc nghiệm & Ghép từ cơ bản):
- Thẻ câu hỏi trắc nghiệm hình ảnh 4 đáp án.
- Dạng bài kéo thả xếp các khối chữ thành câu hoàn chỉnh.
- Hiệu ứng pháo hoa chúc mừng khi trả lời đúng chuỗi 5 câu.

### MÀN HÌNH 4: HỒ SƠ & THÀNH TÍCH (Profile & Achievements)
- Biểu đồ cột thời gian học trong tuần (Thứ 2 - Chủ Nhật).
- Danh hiệu nhận được (VD: *"Bậc thầy từ vựng nhập môn"*, *"Chiến thần Streak 7 ngày"*).
- Lưu trạng thái tiến độ học hoàn toàn trên IndexedDB client.

---

## 🤖 3. PROMPT MẪU CHO GOOGLE STITCH

```text
Design a stunning, modern mobile UI/UX spec for an iOS/Android app named "Learn English Basic".
Visual Style: Modern Editorial with Mint & Emerald Green palette (Primary: #10B981, Secondary: Warm Amber #F59E0B). Clean white card surfaces, subtle 3D gradients, rounded-3xl corners, glassmorphic floating bottom navigation bar.
Key Mobile Screens:
1. Onboarding Screen: Daily study time selection, vibrant 3D illustration, large emerald CTA.
2. Home Dashboard: Gamified learning path nodes with star ratings, current streak counter 🔥, XP progress widget.
3. Multi-Skill Lesson Screen with 5 skill tabs:
   - Learn Mode: Visual vocabulary cards with audio buttons.
   - Practice Mode: Interactive 3D flip flashcard with SRS rating buttons (Again, Hard, Good, Easy).
   - Listening Mode: Audio player with waveform visualizer and fill-in-the-blank input.
   - Speaking Mode: Glowing mic recording icon, circular pronunciation score gauge (0-100%).
   - Quiz Mode: Multiple choice quiz with image options and drag-and-drop word bubble assembly.
4. Profile Screen: Weekly study bar chart, achievement badges, and offline progress indicator.
```
