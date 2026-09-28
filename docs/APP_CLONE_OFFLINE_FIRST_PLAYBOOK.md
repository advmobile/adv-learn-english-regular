# Offline-first app clone playbook

## Completed foundation

- Content is fetched once from the SpeakHub API, committed atomically to Room, and displayed from Room flows.
- `content_sync_state` prevents repeat API fetches when local content is complete.
- A first-run progress screen provides topic/lesson download status before navigation.
- Topics and lessons use API identifiers, including lazy per-topic recovery when a local lesson list is missing.
- Lesson Learn, Flashcard, Listening, Speaking and Quiz are fed from the selected lesson's local content and phrases.
- Flashcard ratings persist a lesson progress event in `user_progress` locally.

## Learning analytics and daily goals

- **Streak** must be calculated from one persisted study-day record per local calendar day; never hard-code it in UI.
- **Total XP** is the sum of persisted learning events. Award XP once per meaningful completion, not merely when a screen is opened.
- **Words mastered** is counted from per-phrase spaced-repetition state. Only `Đã nhớ` and `Rất dễ` qualify as mastered; `Lặp lại` and `Khó` do not.
- **Daily learning goal** is a user setting (5, 10, 15, 20, or 30 minutes). Persist it locally, track elapsed active study time per day, and render the dashboard task from `studiedMinutes / goalMinutes`.
- Profile and Home must observe these local aggregates rather than displaying mock streak, XP, vocabulary, or task values.

### Implementation contract

Create `study_day` with primary key `yyyy-MM-dd`, `active_minutes` and `xp_earned`; create `phrase_progress` keyed by phrase ID with review grade.  On each completed study interaction, upsert today's study row and phrase grade in one Room transaction.  Dashboard aggregates are then: consecutive `study_day` rows ending today/yesterday for streak, sum of `xp_earned` for XP, mastered phrase grades for vocabulary, and today's `active_minutes / daily_goal_minutes` for the daily task.

## Per-clone checklist

1. Change `AppConfig.APP_ID`, API base URL and content language.
2. Keep Room content/sync/user-progress tables and migrations; do not seed UI mock content.
3. Replace only design tokens in `DesignSystem.kt` (primary, emerald/accent, mint/surface, ink) to create the product identity.
4. Preserve `WhiteCard`, rounded controls and clipped/shape-aware interactions for consistent feedback.
5. Add a real profile/progress API only when server-side account sync is available. Until then, present Room-derived progress as device-local progress.

## Data ownership

- API: topics, lessons, content and phrases.
- Room: cached content, sync state, user learning progress.
- UI: observes Room; it must not hard-code lesson titles, XP, tasks or profile totals.
