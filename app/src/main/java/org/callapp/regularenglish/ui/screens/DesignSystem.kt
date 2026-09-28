package org.callapp.regularenglish.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import org.callapp.regularenglish.R
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.remember
import androidx.compose.foundation.clickable
import org.callapp.regularenglish.ads.AdMobBanner
import org.callapp.regularenglish.config.AppBrandPalette
import org.callapp.regularenglish.BuildConfig

@Composable
internal fun Modifier.roundClick(onClick: () -> Unit): Modifier = clickable(
    interactionSource = remember { MutableInteractionSource() }, indication = null, onClick = onClick
)

internal val Emerald = AppBrandPalette.accent
internal val Primary = AppBrandPalette.primary
internal val TealDark = AppBrandPalette.accentDark
internal val Forest = AppBrandPalette.accentDeep
internal val Mint = AppBrandPalette.background
internal val Ink = AppBrandPalette.onBackground
internal val MutedInk = AppBrandPalette.mutedText
internal val SurfaceWhite = AppBrandPalette.surface
internal val SurfaceContainer = AppBrandPalette.surfaceContainer
internal val SoftAccent = AppBrandPalette.softAccent
internal val Gold = AppBrandPalette.secondary
internal val AmberSoft = AppBrandPalette.secondarySoft
internal val AmberText = AppBrandPalette.secondaryText
internal val AmberContainer = AppBrandPalette.secondaryContainer
internal val Error = AppBrandPalette.error
internal val ErrorSoft = AppBrandPalette.errorSoft
internal val AppCardShape = RoundedCornerShape(24.dp)
internal val ControlShape = RoundedCornerShape(16.dp)

@Composable
internal fun AppPage(showBanner: Boolean = true, content: @Composable (PaddingValues) -> Unit) {
    Scaffold(containerColor = Mint) { padding ->
        Box(Modifier.fillMaxSize().background(Mint)) { content(padding) }
    }
}

@Composable
internal fun InlineAdBanner(modifier: Modifier = Modifier) {
    // In screenshot/debug mode omit the entire slot, including its label and empty frame.
    if (!BuildConfig.SHOW_ADS) return
    Column(modifier.padding(vertical = 10.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("ADVERTISEMENT", color = MutedInk, fontSize = 9.sp, fontWeight = FontWeight.Medium, modifier = Modifier.padding(bottom = 4.dp))
        Surface(shape = ControlShape, color = SurfaceWhite, border = BorderStroke(1.dp, Emerald.copy(alpha = .10f))) {
            AdMobBanner(Modifier.fillMaxWidth().height(50.dp))
        }
    }
}

@Composable
internal fun WhiteCard(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Card(
        modifier = modifier.shadow(12.dp, AppCardShape, ambientColor = Emerald.copy(alpha = .16f), spotColor = Color.Black.copy(alpha = .08f)),
        shape = AppCardShape,
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        border = BorderStroke(1.dp, Emerald.copy(alpha = .14f))
    ) { content() }
}

@Composable
internal fun FloatingBottomDock(active: String, onHome: () -> Unit, onPractice: () -> Unit, onReview: () -> Unit, onProfile: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 28.dp, vertical = 16.dp).height(64.dp),
        shape = RoundedCornerShape(32.dp), color = SurfaceWhite.copy(alpha = .94f), shadowElevation = 14.dp
    ) {
        Row(Modifier.fillMaxSize().padding(horizontal = 10.dp), verticalAlignment = Alignment.CenterVertically) {
            DockItem("⌂", stringResource(R.string.home), active == "home", onHome)
            DockItem("◉", stringResource(R.string.practice), active == "practice", onPractice)
            DockItem("↻", stringResource(R.string.review), active == "review", onReview)
            DockItem("profile", stringResource(R.string.profile), active == "profile", onProfile)
        }
    }
}

@Composable
private fun androidx.compose.foundation.layout.RowScope.DockItem(icon: String, label: String, selected: Boolean, onClick: () -> Unit) {
    Surface(modifier = Modifier.weight(1f).height(48.dp), color = if (selected) Emerald.copy(alpha = .14f) else Color.Transparent, shape = RoundedCornerShape(24.dp), onClick = onClick) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = androidx.compose.foundation.layout.Arrangement.Center) {
            if (icon == "profile") ProfileGlyph(if (selected) Primary else MutedInk) else Text(icon, color = if (selected) Primary else MutedInk, fontSize = 18.sp)
            if (selected) { Spacer(Modifier.width(4.dp)); Text(label, color = Primary, fontSize = 10.sp, fontWeight = FontWeight.Bold) }
        }
    }
}

@Composable
internal fun ProfileGlyph(color: Color, modifier: Modifier = Modifier.width(20.dp).height(20.dp)) {
    Canvas(modifier) {
        drawCircle(color = color, radius = size.minDimension * .22f, center = androidx.compose.ui.geometry.Offset(size.width / 2f, size.height * .31f))
        drawRoundRect(color = color, topLeft = androidx.compose.ui.geometry.Offset(size.width * .22f, size.height * .55f), size = androidx.compose.ui.geometry.Size(size.width * .56f, size.height * .33f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(size.width * .22f), style = Fill)
    }
}
