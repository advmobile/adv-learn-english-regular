package org.callapp.regularenglish.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import org.callapp.regularenglish.R

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    var titleVisible by remember { mutableStateOf(false) }
    var titleLeaving by remember { mutableStateOf(false) }
    val titleAlpha by animateFloatAsState(
        targetValue = when { titleLeaving -> 0f; titleVisible -> 1f; else -> 0f },
        animationSpec = tween(if (titleLeaving) 420 else 520), label = "splash_title_alpha"
    )
    val titleOffset by animateFloatAsState(
        targetValue = if (titleLeaving) -64f else 0f,
        animationSpec = tween(520), label = "splash_title_offset"
    )

    LaunchedEffect(Unit) {
        delay(180)
        titleVisible = true
        delay(1250)
        titleLeaving = true
        delay(500)
        onFinished()
    }
    Box(Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.splash),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Text(
            text = stringResource(R.string.splash_title),
            color = Color.White,
            fontSize = 34.sp,
            lineHeight = 46.sp,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Alignment.Center).offset(y = titleOffset.dp).alpha(titleAlpha)
                .padding(horizontal = 24.dp)
        )
    }
}
