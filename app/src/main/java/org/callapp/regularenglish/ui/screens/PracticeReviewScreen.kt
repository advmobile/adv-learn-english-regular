package org.callapp.regularenglish.ui.screens

import android.app.Activity
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import org.callapp.regularenglish.data.repository.LearningAnalyticsRepository
import org.callapp.regularenglish.data.repository.LearningProgressRepository
import org.callapp.regularenglish.domain.repository.ContentRepository
import org.callapp.regularenglish.domain.model.Vocabulary
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.input.pointer.pointerInput
import org.callapp.regularenglish.ads.AdMobManager
import java.util.Locale

@Composable
fun PracticeReviewScreen(isReview: Boolean, onHome: () -> Unit, onPractice: () -> Unit, onReview: () -> Unit, onLearn: () -> Unit, onProfile: () -> Unit) {
    val title = if (isReview) "Review" else "Daily Practice"
    val message = if (isReview) "Your scheduled flashcard review will appear here." else "Your daily mixed practice will appear here."
    AppPage { padding -> Box(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize().padding(padding).padding(horizontal = 20.dp).padding(top = 20.dp, bottom = 120.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            WhiteCard(Modifier.fillMaxWidth()) { Column(Modifier.padding(28.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(if (isReview) "↻" else "◉", color = Primary, fontSize = 32.sp)
                Text(title, color = Ink, fontSize = 24.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 12.dp))
                Text(message, color = MutedInk, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 8.dp))
            } }
        }
        Box(Modifier.align(Alignment.BottomCenter).padding(padding)) { FloatingBottomDock(if (isReview) "review" else "practice", onHome, onPractice, onReview, onProfile) }
    } }
}

@Composable
fun DailyPracticeScreen(repository: ContentRepository, progressRepository: LearningProgressRepository, analyticsRepository: LearningAnalyticsRepository, onHome: () -> Unit, onPractice: () -> Unit, onReview: () -> Unit, onLearn: () -> Unit, onProfile: () -> Unit) {
    val progress by progressRepository.observeAll().collectAsState(initial = emptyList())
    val vocabulary by repository.observeAllVocabulary().collectAsState(initial = emptyList())
    val studiedLessonIds = progress.filter { it.completionPercentage > 0 }.map { it.lessonId }.toSet()
    val dailySet = remember(vocabulary, studiedLessonIds) { vocabulary.filter { it.lessonId in studiedLessonIds }.shuffled().take(5) }
    var index by remember(dailySet) { mutableIntStateOf(0) }
    val scope = rememberCoroutineScope()
    val activity = LocalContext.current as? Activity
    val phrase = dailySet.getOrNull(index)
    AppPage { padding -> Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
        Column(
            Modifier.fillMaxWidth().fillMaxHeight().widthIn(max = 360.dp)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                // The dock is an overlay, so this keeps the enabled Next button reachable.
                .padding(bottom = 128.dp, top = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
        ) {
            if (phrase == null) WhiteCard(Modifier.fillMaxWidth()) { Column(Modifier.padding(28.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("◉", color = Primary, fontSize = 32.sp)
                Text("Daily Practice", color = Ink, fontSize = 24.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 12.dp))
                Text("Complete a lesson first. Daily Practice then mixes phrases from lessons you have studied.", color = MutedInk, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 8.dp))
                Button(onClick = onLearn, modifier = Modifier.padding(top = 20.dp), shape = ControlShape, colors = ButtonDefaults.buttonColors(containerColor = Primary)) { Text("GO TO LEARN") }
            } } else {
                // The five steps intentionally match the summary chips: Listen ×2, Speak ×1, Quiz ×2.
                val type = listOf("LISTEN", "SPEAK", "QUIZ", "LISTEN", "QUIZ")[index % 5]
                var interactionComplete by remember(phrase.id, index) { mutableStateOf(false) }
                val options = remember(phrase.id, index, dailySet) {
                    (listOf(phrase.meaning) + dailySet.filter { it.id != phrase.id }.map { it.meaning })
                        .filter { it.isNotBlank() }
                        .distinct()
                        .shuffled()
                        .take(4)
                }
                Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("DAILY PRACTICE", color = Ink, fontSize = 24.sp, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center)
                    Text("A focused 3-minute session", color = MutedInk, fontSize = 12.sp, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 3.dp))
                    androidx.compose.foundation.layout.Box(Modifier.padding(top = 12.dp), contentAlignment = Alignment.Center) { androidx.compose.material3.CircularProgressIndicator(progress = { (index + 1).toFloat() / dailySet.size }, modifier = Modifier.size(48.dp), color = Emerald, trackColor = Emerald.copy(alpha = .14f)); Text("${index + 1}/${dailySet.size}", color = Primary, fontSize = 10.sp, fontWeight = FontWeight.SemiBold) }
                }
                androidx.compose.foundation.layout.Row(Modifier.fillMaxWidth().padding(top = 18.dp), horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)) { listOf("🎧  Listen 2", "🎙  Speak 1", "✓  Quiz 2").forEach { label -> androidx.compose.material3.Surface(modifier = Modifier.weight(1f), shape = ControlShape, color = if (label.contains(type.lowercase().replaceFirstChar { it.uppercase() })) Emerald.copy(alpha = .16f) else SurfaceWhite) { Text(label, Modifier.padding(vertical = 9.dp, horizontal = 4.dp), color = TealDark, textAlign = TextAlign.Center, fontSize = 10.sp, fontWeight = FontWeight.SemiBold) } } }
                InlineAdBanner(Modifier.padding(top = 18.dp))
                androidx.compose.foundation.layout.Spacer(Modifier.height(18.dp))
                PracticeInteraction(type, phrase, options) { interactionComplete = true }
                androidx.compose.foundation.layout.Row(Modifier.padding(top = 18.dp), horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)) { repeat(dailySet.size) { step -> androidx.compose.material3.Surface(modifier = Modifier.size(if (step == index) 24.dp else 10.dp, 10.dp), shape = ControlShape, color = if (step <= index) Emerald else Emerald.copy(alpha = .18f)) {} } }
                androidx.compose.material3.Surface(Modifier.fillMaxWidth().padding(top = 18.dp), shape = ControlShape, color = AmberSoft.copy(alpha = .64f)) { Text("Today’s reward  ·  +15 XP  ·  3 min", Modifier.padding(vertical = 12.dp), color = AmberText, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center) }
                Button(enabled = interactionComplete, onClick = { if (index < dailySet.lastIndex) index++ else { scope.launch { analyticsRepository.recordStudy(3, 15) }; AdMobManager.showInterstitial(activity) { index = 0 } } }, modifier = Modifier.fillMaxWidth().padding(top = 14.dp).height(52.dp), shape = ControlShape, colors = ButtonDefaults.buttonColors(containerColor = Primary, disabledContainerColor = Emerald.copy(alpha = .22f), disabledContentColor = TealDark.copy(alpha = .55f))) { Text(if (interactionComplete) { if (index == dailySet.lastIndex) "COMPLETE PRACTICE" else "NEXT  ›" } else "COMPLETE THIS STEP") }
            }
        }
        Box(Modifier.align(Alignment.BottomCenter)) { FloatingBottomDock("practice", onHome, onPractice, onReview, onProfile) }
    } }
}

@Composable
private fun PracticeInteraction(type: String, phrase: Vocabulary, options: List<String>, onCompleted: () -> Unit) {
    when (type) {
        "LISTEN" -> PracticeListen(phrase, options, onCompleted)
        "SPEAK" -> PracticeSpeak(phrase, onCompleted)
        else -> PracticeQuiz(phrase, options, onCompleted)
    }
}

@Composable
private fun PracticeListen(phrase: Vocabulary, options: List<String>, onCompleted: () -> Unit) {
    val context = LocalContext.current
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }
    var played by remember(phrase.id) { mutableStateOf(false) }
    var selected by remember(phrase.id) { mutableStateOf<String?>(null) }
    DisposableEffect(Unit) {
        lateinit var engine: TextToSpeech
        engine = TextToSpeech(context) { if (it == TextToSpeech.SUCCESS) engine.language = Locale.US }
        tts = engine
        onDispose { engine.shutdown() }
    }
    WhiteCard(Modifier.fillMaxWidth()) { Column(Modifier.fillMaxWidth().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("LISTEN", color = Primary, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
        Text("Listen, then choose the meaning", color = Ink, fontSize = 18.sp, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 12.dp))
        Button(onClick = { played = true; tts?.speak(phrase.word, TextToSpeech.QUEUE_FLUSH, null, "daily_listen") }, modifier = Modifier.padding(top = 18.dp).height(48.dp), shape = ControlShape, colors = ButtonDefaults.buttonColors(containerColor = Emerald)) { Text("▶  PLAY SENTENCE", fontWeight = FontWeight.Bold) }
        if (played) {
            Text("What did you hear?", color = MutedInk, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 18.dp))
            options.forEach { option -> PracticeOption(option, selected, phrase.meaning) { selected = it; onCompleted() } }
            selected?.let { PracticeAnswer(if (it == phrase.meaning) "✓ Correct!" else "Answer: ${phrase.meaning}", it == phrase.meaning) }
        } else Text("Play the sentence to reveal the choices.", color = MutedInk, textAlign = TextAlign.Center, fontSize = 12.sp, modifier = Modifier.padding(top = 14.dp))
    } }
}

@Composable
private fun PracticeQuiz(phrase: Vocabulary, options: List<String>, onCompleted: () -> Unit) {
    var selected by remember(phrase.id) { mutableStateOf<String?>(null) }
    WhiteCard(Modifier.fillMaxWidth()) { Column(Modifier.fillMaxWidth().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("QUIZ", color = Primary, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
        Text("What is the correct meaning?", color = Ink, fontSize = 18.sp, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 12.dp))
        androidx.compose.material3.Surface(Modifier.padding(top = 16.dp), shape = ControlShape, color = Emerald.copy(alpha = .12f)) { Text(phrase.word, Modifier.padding(horizontal = 18.dp, vertical = 12.dp), color = TealDark, fontSize = 19.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center) }
        options.forEach { option -> PracticeOption(option, selected, phrase.meaning) { selected = it; onCompleted() } }
        selected?.let { PracticeAnswer(if (it == phrase.meaning) "✓ Correct!" else "Answer: ${phrase.meaning}", it == phrase.meaning) }
    } }
}

@Composable
private fun PracticeOption(option: String, selected: String?, answer: String, onChoose: (String) -> Unit) {
    val isSelected = selected == option
    val color = when { !isSelected -> SurfaceWhite; option == answer -> Emerald.copy(alpha = .18f); else -> ErrorSoft }
    androidx.compose.material3.Surface(onClick = { if (selected == null) onChoose(option) }, modifier = Modifier.fillMaxWidth().padding(top = 9.dp), shape = ControlShape, color = color, border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) Emerald.copy(alpha = .55f) else Emerald.copy(alpha = .16f))) {
        Text(option, Modifier.fillMaxWidth().padding(vertical = 13.dp, horizontal = 12.dp), color = Ink, textAlign = TextAlign.Center, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun PracticeAnswer(text: String, correct: Boolean) = Text(text, color = if (correct) Emerald else Error, textAlign = TextAlign.Center, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth().padding(top = 14.dp))

@Composable
private fun PracticeSpeak(phrase: Vocabulary, onCompleted: () -> Unit) {
    val context = LocalContext.current
    var recording by remember(phrase.id) { mutableStateOf(false) }
    var score by remember(phrase.id) { mutableStateOf<Int?>(null) }
    var recognizer by remember { mutableStateOf<SpeechRecognizer?>(null) }
    fun startRecognition() {
        if (!SpeechRecognizer.isRecognitionAvailable(context)) return
        recognizer?.destroy()
        recognizer = SpeechRecognizer.createSpeechRecognizer(context).also { recognizer ->
            recognizer.setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: android.os.Bundle?) { recording = true }
                override fun onResults(results: android.os.Bundle?) {
                    val heard = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull().orEmpty()
                    val words = { input: String -> input.lowercase().replace(Regex("[^a-z0-9 ]"), "").split(" ").filter(String::isNotBlank).toSet() }
                    val expected = words(phrase.word); val actual = words(heard)
                    score = if (expected.isEmpty()) 0 else (expected.intersect(actual).size * 100 / expected.size)
                    recording = false; onCompleted()
                }
                override fun onError(error: Int) { recording = false }
                override fun onBeginningOfSpeech() {} ; override fun onBufferReceived(buffer: ByteArray?) {} ; override fun onEndOfSpeech() { recording = false }; override fun onEvent(eventType: Int, params: android.os.Bundle?) {} ; override fun onPartialResults(partialResults: android.os.Bundle?) {} ; override fun onRmsChanged(rmsdB: Float) {}
            })
            recognizer.startListening(Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US").putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM))
        }
    }
    val permission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { if (it) startRecognition() }
    DisposableEffect(Unit) { onDispose { recognizer?.destroy() } }
    WhiteCard(Modifier.fillMaxWidth()) { Column(Modifier.fillMaxWidth().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("SPEAK", color = Primary, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
        Text(phrase.word, color = Ink, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(top = 14.dp))
        Text("Hold the microphone and repeat the phrase", color = MutedInk, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 8.dp))
        androidx.compose.material3.Surface(Modifier.padding(top = 18.dp).size(116.dp).pointerInput(phrase.id) { detectTapGestures(onPress = { if (context.checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) startRecognition() else permission.launch(Manifest.permission.RECORD_AUDIO); tryAwaitRelease(); recognizer?.stopListening() }) }, shape = androidx.compose.foundation.shape.CircleShape, color = if (recording) Error.copy(alpha = .2f) else Emerald.copy(alpha = .16f)) { Box(contentAlignment = Alignment.Center) { Text(if (recording) "●" else "🎙", color = if (recording) Error else Ink, fontSize = 42.sp) } }
        Text(if (recording) "Listening… release to score" else "HOLD TO SPEAK", color = TealDark, fontSize = 12.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 12.dp))
        score?.let { PracticeAnswer("Pronunciation match: $it%", it >= 80) }
    } }
}

@Composable
fun ReviewScreen(repository: ContentRepository, analyticsRepository: LearningAnalyticsRepository, onHome: () -> Unit, onPractice: () -> Unit, onReview: () -> Unit, onLearn: () -> Unit, onProfile: () -> Unit) {
    val phrases by repository.observeAllVocabulary().collectAsState(initial = emptyList())
    val lessons by repository.observeAllLessons().collectAsState(initial = emptyList())
    val phraseProgress by analyticsRepository.observePhraseProgress().collectAsState(initial = emptyList())
    var filter by remember { mutableStateOf("DUE TODAY") }
    var selectedLessonId by remember { mutableStateOf<String?>(null) }
    var index by remember(filter, selectedLessonId) { mutableIntStateOf(0) }
    var flipped by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val progressById = phraseProgress.associateBy { it.phraseId }
    val learned = phrases.filter { progressById.containsKey(it.id) }
    val candidates = when (filter) {
        "DUE TODAY" -> learned.filter { progressById[it.id]?.grade == "REVIEW" }
        "ALL LEARNED" -> learned
        else -> learned.filter { it.lessonId == selectedLessonId }
    }
    val phrase = candidates.getOrNull(index % candidates.size.coerceAtLeast(1))
    AppPage { padding -> Box(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize().padding(padding).padding(horizontal = 20.dp).padding(bottom = 88.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center) {
            Text("Review", color = Ink, fontSize = 24.sp, fontWeight = FontWeight.SemiBold)
            Text("Revisit phrases that need attention.", color = MutedInk, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 4.dp))
            androidx.compose.foundation.layout.Row(Modifier.fillMaxWidth().padding(top = 18.dp), horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(6.dp)) {
                listOf("DUE TODAY", "ALL LEARNED", "BY LESSON").forEach { item -> androidx.compose.material3.Surface(onClick = { filter = item; if (item == "BY LESSON") selectedLessonId = lessons.firstOrNull()?.id }, modifier = Modifier.weight(1f), shape = ControlShape, color = if (filter == item) Primary else SurfaceWhite) { Text(item.replace(" ", "\n"), Modifier.padding(vertical = 9.dp, horizontal = 3.dp), color = if (filter == item) Color.White else MutedInk, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center) } }
            }
            InlineAdBanner(Modifier.padding(top = 16.dp))
            if (filter == "BY LESSON") Text(lessons.firstOrNull { it.id == selectedLessonId }?.title ?: "Select a lesson", color = TealDark, fontSize = 12.sp, modifier = Modifier.padding(top = 10.dp))
            if (phrase == null) WhiteCard(Modifier.fillMaxWidth().padding(top = 18.dp)) { Column(Modifier.padding(28.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("No phrases to review", color = Ink, fontSize = 19.sp, fontWeight = FontWeight.SemiBold)
                Text(if (filter == "DUE TODAY") "Rate a flashcard as Again or Hard to add it to Due today." else "Study and rate flashcards first to build your review set.", color = MutedInk, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 8.dp))
                Button(onClick = onLearn, modifier = Modifier.padding(top = 20.dp), shape = ControlShape, colors = ButtonDefaults.buttonColors(containerColor = Primary)) { Text("GO TO LEARN") }
            } } else ReviewCard(phrase, flipped, { flipped = !flipped }) { mastered -> scope.launch { analyticsRepository.ratePhrase(phrase.id, mastered) }; flipped = false; index++ }
        }
        Box(Modifier.align(Alignment.BottomCenter).padding(padding)) { FloatingBottomDock("review", onHome, onPractice, onReview, onProfile) }
    } }
}

@Composable
private fun ReviewCard(phrase: Vocabulary, flipped: Boolean, onFlip: () -> Unit, onRate: (Boolean) -> Unit) {
    WhiteCard(Modifier.fillMaxWidth().padding(top = 18.dp).roundClick(onFlip)) { Column(Modifier.fillMaxWidth().padding(28.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(if (flipped) phrase.meaning else phrase.word, color = Ink, fontSize = 24.sp, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center)
        Text(if (flipped) "Choose how well you remembered it." else "Tap to reveal the meaning", color = MutedInk, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 14.dp))
        if (flipped) androidx.compose.foundation.layout.Row(Modifier.fillMaxWidth().padding(top = 20.dp), horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)) {
            listOf("Again" to false, "Hard" to false, "Remembered" to true, "Very easy" to true).forEach { (label, mastered) -> androidx.compose.material3.Surface(onClick = { onRate(mastered) }, modifier = Modifier.weight(1f), shape = ControlShape, color = if (mastered) Emerald.copy(alpha = .16f) else AmberSoft) { Text(label, Modifier.padding(vertical = 12.dp, horizontal = 4.dp), color = Ink, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center) } }
        }
    } }
}
