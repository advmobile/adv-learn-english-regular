package org.callapp.regularenglish.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import org.callapp.regularenglish.config.AppBrandPalette

private val DarkColorScheme = darkColorScheme(
    primary = AppBrandPalette.accent, secondary = AppBrandPalette.secondary,
    background = AppBrandPalette.accentDeep, surface = AppBrandPalette.primary,
    onBackground = AppBrandPalette.softAccent, onSurface = AppBrandPalette.softAccent
)

private val LightColorScheme = lightColorScheme(
    primary = AppBrandPalette.primary, onPrimary = Color.White,
    primaryContainer = AppBrandPalette.accent, onPrimaryContainer = AppBrandPalette.accentDeep,
    secondary = AppBrandPalette.secondaryText, secondaryContainer = AppBrandPalette.secondarySoft,
    background = AppBrandPalette.background, surface = AppBrandPalette.surface,
    surfaceVariant = AppBrandPalette.surfaceContainer, onBackground = AppBrandPalette.onBackground,
    onSurface = AppBrandPalette.onBackground
)

@Composable
fun LearnEnglishRegularTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
