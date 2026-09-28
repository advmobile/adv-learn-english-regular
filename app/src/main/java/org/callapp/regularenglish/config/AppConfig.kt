package org.callapp.regularenglish.config

import androidx.compose.ui.graphics.Color

/** The only app-specific configuration required when cloning an AdvMobile app. */
object AppConfig {
    const val APP_ID = "learn-english-regular"
    const val APP_NAME = "Learn English Regular"
    const val APP_TYPE = "adv"
    const val CONTENT_LANGUAGE = "vi"
    const val API_BASE_URL = "https://api.speakhub.org/api/apps/learn-english-regular/"
    const val API_SCHEMA_VERSION = 1
}

/**
 * The single visual-brand entry point for an app clone.
 *
 * Change these values to give a clone its own identity. UI components and the
 * Material theme consume this palette rather than defining brand colours locally.
 * Error is semantic (not a brand accent), while the remaining values can be
 * adjusted together for a coherent new tone.
 */
object AppBrandPalette {
    val primary = Color(0xFF4037A8)
    val accent = Color(0xFF7066E5)
    val accentDark = Color(0xFF342D8A)
    val accentDeep = Color(0xFF241C66)
    val background = Color(0xFFF8F7FF)
    val onBackground = Color(0xFF171338)
    val mutedText = Color(0xFF615E7C)
    val surface = Color.White
    val surfaceContainer = Color(0xFFEAE8FF)
    val softAccent = Color(0xFFDFDCFF)
    val secondary = Color(0xFFFFC247)
    val secondarySoft = Color(0xFFFFEBBF)
    val secondaryText = Color(0xFF815C05)
    val secondaryContainer = Color(0xFFFFF5DB)
    val error = Color(0xFFEF4444)
    val errorSoft = Color(0xFFFFE2E2)
}
