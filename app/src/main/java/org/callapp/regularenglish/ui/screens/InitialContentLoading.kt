package org.callapp.regularenglish.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.callapp.regularenglish.data.repository.ContentSyncRepository

@Composable
fun InitialContentLoading(syncRepository: ContentSyncRepository, onContentReady: () -> Unit) {
    val progress by syncRepository.progress.collectAsState()
    val animatedProgress by animateFloatAsState(progress.fraction, label = "initial_content_progress")
    var attempt by remember { mutableStateOf(0) }
    var failed by remember { mutableStateOf(false) }
    LaunchedEffect(attempt) {
        failed = false
        syncRepository.prepareInitialContent()
            .onSuccess { onContentReady() }
            .onFailure { failed = true }
    }
    AppPage(showBanner = false) { padding ->
        Box(Modifier.fillMaxSize().padding(padding).padding(horizontal = 28.dp), contentAlignment = Alignment.Center) {
            WhiteCard(Modifier.fillMaxWidth()) {
                Column(Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 30.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🌱", fontSize = 42.sp)
                    Text("Preparing your learning journey", color = Ink, fontSize = 23.sp, fontWeight = FontWeight.ExtraBold, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 14.dp))
                    Text(if (failed) "We couldn't prepare your lessons. Check your connection and try again." else "Your lessons are being downloaded and saved for offline learning.", color = MutedInk, textAlign = TextAlign.Center, fontSize = 14.sp, lineHeight = 21.sp, modifier = Modifier.padding(top = 9.dp))
                    if (failed) {
                        Text("Unable to download lessons", color = Error, fontWeight = FontWeight.Bold, fontSize = 14.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(top = 24.dp))
                        progress.errorMessage?.let { error ->
                            Text(error, color = MutedInk, fontSize = 12.sp, textAlign = TextAlign.Center, lineHeight = 17.sp, modifier = Modifier.fillMaxWidth().padding(top = 8.dp))
                        }
                        Button(onClick = { attempt++ }, modifier = Modifier.fillMaxWidth().padding(top = 18.dp).height(52.dp), shape = ControlShape, colors = ButtonDefaults.buttonColors(containerColor = Primary, contentColor = SurfaceWhite)) {
                            Text("TRY AGAIN", fontWeight = FontWeight.ExtraBold)
                        }
                    } else {
                        LinearProgressIndicator(progress = { animatedProgress }, modifier = Modifier.fillMaxWidth().padding(top = 24.dp).height(8.dp), color = Emerald, trackColor = SurfaceContainer)
                        Text(progress.message, color = Primary, fontWeight = FontWeight.Bold, fontSize = 13.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(top = 14.dp))
                        if (progress.isRunning) Text("${progress.completed}/${progress.total}", color = MutedInk, fontSize = 12.sp, modifier = Modifier.padding(top = 5.dp))
                    }
                }
            }
        }
    }
}
