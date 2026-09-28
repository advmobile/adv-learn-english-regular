package org.callapp.regularenglish.ui.screens

import android.speech.tts.TextToSpeech
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.core.text.HtmlCompat
import org.callapp.regularenglish.domain.model.Vocabulary
import org.callapp.regularenglish.domain.repository.ContentRepository
import androidx.compose.ui.res.stringResource
import org.callapp.regularenglish.R
import org.callapp.regularenglish.data.repository.LearningProgressRepository
import org.callapp.regularenglish.data.repository.LearningAnalyticsRepository
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun LessonScreen(repository: ContentRepository, progressRepository: LearningProgressRepository, analyticsRepository: LearningAnalyticsRepository, onBack: () -> Unit, lessonId: String) {
    val lesson by repository.observeLesson(lessonId).collectAsState(initial = null)
    val phrases by repository.observeVocabulary(lessonId).collectAsState(initial = emptyList())
    var tab by remember { mutableIntStateOf(0) }
    val scope = rememberCoroutineScope()
    LaunchedEffect(lessonId) { if (lessonId.isNotBlank()) { progressRepository.recordStudy(lessonId, 10, 0); analyticsRepository.recordStudy(1, 0) } }
    AppPage { padding -> Column(Modifier.fillMaxSize().padding(padding).padding(horizontal = 20.dp).verticalScroll(rememberScrollState()).padding(bottom = 28.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("‹  ${stringResource(R.string.back)}", color = Primary, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth().clickable { onBack() })
        Text("LESSON SESSION", color = Primary, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(top = 14.dp))
        Text(lesson?.title ?: "Loading lesson…", color = Ink, fontSize = 23.sp, fontWeight = FontWeight.ExtraBold, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 5.dp))
        lesson?.description?.takeIf { it.isNotBlank() }?.let { Text(it, color = MutedInk, fontSize = 13.sp, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 4.dp)) }
        LinearProgressIndicator(progress = { 0f }, Modifier.fillMaxWidth().padding(vertical = 14.dp), color = Emerald, trackColor = SurfaceContainer)
        InlineAdBanner()
        Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) { listOf("📖  ${stringResource(R.string.learn)}", "🎴  ${stringResource(R.string.flashcard)}", "🎧  ${stringResource(R.string.listening)}", "🎙  ${stringResource(R.string.speaking)}", "🧪  ${stringResource(R.string.quiz)}").forEachIndexed { index, name -> Surface(onClick = { tab = index }, modifier = Modifier.height(42.dp), shape = androidx.compose.foundation.shape.CircleShape, color = if (tab == index) Primary else SurfaceWhite, shadowElevation = if (tab == index) 5.dp else 1.dp) { Box(Modifier.padding(horizontal = 16.dp).fillMaxHeight(), contentAlignment = Alignment.Center) { Text(name, color = if (tab == index) Color.White else MutedInk, fontSize = 12.sp, fontWeight = if (tab == index) FontWeight.ExtraBold else FontWeight.SemiBold) } } } }
        if (lesson == null) EmptyData("Loading lesson content…") else when (tab) { 0 -> Learn(lesson!!.content, phrases); 1 -> Flashcards(phrases) { phraseId, mastered -> scope.launch { progressRepository.recordStudy(lessonId, 50, 10); analyticsRepository.recordStudy(1, 10); analyticsRepository.ratePhrase(phraseId, mastered) } }; 2 -> Listening(phrases.firstOrNull()); 3 -> Speaking(phrases.firstOrNull()); else -> QuizGame(phrases) { scope.launch { progressRepository.recordStudy(lessonId, 100, 20); analyticsRepository.recordStudy(2, 20) } } }
    } }
}

@Composable private fun Learn(content: String, phrases: List<Vocabulary>) { val text = remember(content) { HtmlCompat.fromHtml(content, HtmlCompat.FROM_HTML_MODE_COMPACT).toString().trim() }; Column(Modifier.fillMaxWidth().padding(top = 20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) { WhiteCard(Modifier.fillMaxWidth()) { Column(Modifier.padding(20.dp)) { Text(stringResource(R.string.lesson_content), color = Primary, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold); Text(if (text.isBlank()) "Content is being updated." else text, color = Ink, fontSize = 16.sp, lineHeight = 25.sp, modifier = Modifier.padding(top = 12.dp)) } }; if (phrases.isNotEmpty()) WhiteCard(Modifier.fillMaxWidth()) { Column(Modifier.padding(20.dp)) { Text("KEY SENTENCES", color = Primary, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold); phrases.forEachIndexed { i, p -> Text("${i + 1}. ${p.word}", color = Ink, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 13.dp)); if (p.meaning.isNotBlank()) Text(p.meaning, color = MutedInk, fontSize = 13.sp, modifier = Modifier.padding(top = 3.dp)) } } } } }

@Composable private fun Flashcards(phrases: List<Vocabulary>, onRated: () -> Unit) { var index by remember(phrases) { mutableIntStateOf(0) }; var flipped by remember { mutableStateOf(false) }; val p = phrases.getOrNull(index); if (p == null) EmptyData("No phrases available for flashcards.") else Column(Modifier.fillMaxWidth().padding(top = 20.dp), horizontalAlignment = Alignment.CenterHorizontally) { Text("CARD ${index + 1}/${phrases.size}", color = Primary, fontWeight = FontWeight.ExtraBold, fontSize = 12.sp); WhiteCard(Modifier.fillMaxWidth().height(320.dp).padding(top = 12.dp).clickable { flipped = !flipped }) { Box(Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) { Text(if (flipped) p.meaning else p.word, textAlign = TextAlign.Center, color = Ink, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold) } }; Text(if (flipped) "How well do you remember this?" else "Tap the card to reveal the meaning", color = MutedInk, modifier = Modifier.padding(top = 16.dp)); if (flipped) Row(Modifier.fillMaxWidth().padding(top = 12.dp), horizontalArrangement = Arrangement.spacedBy(7.dp)) { listOf("↻|Again|EF4444", "!|Hard|F59E0B", "✓|Remembered|009B6A", "★|Very easy|3B82F6").forEach { spec -> val (icon, label, hex) = spec.split("|"); val color = Color(hex.toLong(16) or 0xFF000000); Surface(Modifier.weight(1f).height(58.dp).clickable { onRated(); flipped = false; index = (index + 1) % phrases.size }, shape = ControlShape, color = color.copy(alpha = .17f), border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = .42f))) { Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) { Text(icon, color = color, fontWeight = FontWeight.ExtraBold); Text(label, color = color, fontSize = 10.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center) } } } } } }

@Composable private fun Listening(p: Vocabulary?) { if (p == null) EmptyData("No listening sentence.") else { val context = LocalContext.current; var tts by remember { mutableStateOf<TextToSpeech?>(null) }; DisposableEffect(Unit) { lateinit var engine: TextToSpeech; engine = TextToSpeech(context) { if (it == TextToSpeech.SUCCESS) engine.language = Locale.US }; tts = engine; onDispose { engine.shutdown() } }; PracticeCard(stringResource(R.string.listening).uppercase(), p, "▶  ${stringResource(R.string.play_sentence)}") { tts?.speak(p.word, TextToSpeech.QUEUE_FLUSH, null, "lesson_phrase") } } }
@Composable private fun Speaking(p: Vocabulary?) {
    if (p == null) { EmptyData("No phrases available for speaking practice."); return }
    val context = LocalContext.current
    var recording by remember { mutableStateOf(false) }
    var score by remember { mutableStateOf<Int?>(null) }
    var recognizer by remember { mutableStateOf<SpeechRecognizer?>(null) }
    fun startRecognition() {
        if (!SpeechRecognizer.isRecognitionAvailable(context)) return
        val listener = object : RecognitionListener {
            override fun onReadyForSpeech(params: android.os.Bundle?) { recording = true }
            override fun onResults(results: android.os.Bundle?) {
                val heard = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull().orEmpty()
                val expected = p.word.lowercase().replace(Regex("[^a-z0-9 ]"), "").split(" ").filter { it.isNotBlank() }.toSet()
                val actual = heard.lowercase().replace(Regex("[^a-z0-9 ]"), "").split(" ").filter { it.isNotBlank() }.toSet()
                score = if (expected.isEmpty()) 0 else ((expected.intersect(actual).size * 100f) / expected.size).toInt()
                recording = false
            }
            override fun onError(error: Int) { recording = false }
            override fun onBeginningOfSpeech() {} ; override fun onBufferReceived(buffer: ByteArray?) {} ; override fun onEndOfSpeech() { recording = false }; override fun onEvent(eventType: Int, params: android.os.Bundle?) {} ; override fun onPartialResults(partialResults: android.os.Bundle?) {} ; override fun onRmsChanged(rmsdB: Float) {}
        }
        recognizer?.destroy()
        recognizer = SpeechRecognizer.createSpeechRecognizer(context).also { it.setRecognitionListener(listener); it.startListening(Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US").putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)) }
    }
    val permission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted -> if (granted) startRecognition() }
    DisposableEffect(Unit) { onDispose { recognizer?.destroy() } }
    Column(Modifier.fillMaxWidth().padding(top = 20.dp), horizontalAlignment = Alignment.CenterHorizontally) { WhiteCard(Modifier.fillMaxWidth()) { Column(Modifier.fillMaxWidth().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("SPEAKING", color = Primary, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold); Text(p.word, color = Ink, textAlign = TextAlign.Center, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(top = 18.dp)); Text(p.meaning, color = MutedInk, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 7.dp))
        Surface(Modifier.padding(top = 24.dp).size(150.dp).pointerInput(Unit) { detectTapGestures(onPress = { if (context.checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) startRecognition() else permission.launch(Manifest.permission.RECORD_AUDIO); tryAwaitRelease(); recognizer?.stopListening() }) }, shape = androidx.compose.foundation.shape.CircleShape, color = if (recording) Error.copy(alpha = .2f) else Emerald.copy(alpha = .15f)) { Box(contentAlignment = Alignment.Center) { Text(if (recording) "●" else "🎙", color = if (recording) Error else Ink, fontSize = 46.sp) } }
        Text(if (recording) "Listening… release to score" else stringResource(R.string.hold_mic), color = Ink, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 18.dp)); score?.let { Text("Accuracy: $it%", color = if (it >= 80) Emerald else Gold, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(top = 10.dp)) }
    } } }
}
@Composable private fun PracticeCard(label: String, p: Vocabulary, button: String, onClick: () -> Unit) { Column(Modifier.fillMaxWidth().padding(top = 20.dp), horizontalAlignment = Alignment.CenterHorizontally) { WhiteCard(Modifier.fillMaxWidth()) { Column(Modifier.fillMaxWidth().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) { Text(label, color = Primary, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, textAlign = TextAlign.Center); Surface(Modifier.padding(top = 20.dp).size(86.dp), shape = androidx.compose.foundation.shape.CircleShape, color = Emerald.copy(alpha = .14f)) { Box(contentAlignment = Alignment.Center) { Text(if (label == "LISTENING") "🎧" else "🎙", fontSize = 38.sp) } }; Text(p.word, color = Ink, textAlign = TextAlign.Center, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.fillMaxWidth().padding(top = 18.dp)); if (p.meaning.isNotBlank()) Text(p.meaning, color = MutedInk, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(top = 7.dp)); Button(onClick = onClick, modifier = Modifier.padding(top = 24.dp).height(52.dp), shape = ControlShape, colors = ButtonDefaults.buttonColors(containerColor = Emerald)) { Text(button, fontWeight = FontWeight.ExtraBold) } } } } }
@Composable private fun Quiz(phrases: List<Vocabulary>) { val answer = phrases.firstOrNull(); var selected by remember { mutableStateOf<String?>(null) }; if (answer == null) EmptyData("No phrases available for a quiz.") else Column(Modifier.fillMaxWidth().padding(top = 20.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp)) { WhiteCard(Modifier.fillMaxWidth()) { Column(Modifier.fillMaxWidth().padding(22.dp), horizontalAlignment = Alignment.CenterHorizontally) { Text("QUIZ • 01", color = Primary, fontWeight = FontWeight.ExtraBold); Text("What is the correct meaning of this phrase?", color = Ink, textAlign = TextAlign.Center, fontSize = 19.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(top = 12.dp)); Surface(Modifier.padding(top = 14.dp), shape = ControlShape, color = Emerald.copy(alpha = .1f)) { Text(answer.word, Modifier.padding(14.dp), color = TealDark, textAlign = TextAlign.Center, fontSize = 17.sp, fontWeight = FontWeight.Bold) } } }; (listOf(answer.meaning) + phrases.drop(1).take(3).map { it.meaning }).distinct().forEach { option -> Surface(Modifier.fillMaxWidth().clickable { selected = option }, shape = ControlShape, color = if (selected == option) Mint else SurfaceWhite, border = androidx.compose.foundation.BorderStroke(1.dp, if (selected == option) Emerald.copy(alpha = .45f) else Emerald.copy(alpha = .14f))) { Text(option, Modifier.fillMaxWidth().padding(17.dp), textAlign = TextAlign.Center, color = Ink, fontWeight = FontWeight.Bold) } } } }
@Composable private fun EmptyData(message: String) { Text(message, color = MutedInk, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(top = 32.dp)) }

@Composable private fun Flashcards(phrases: List<Vocabulary>, onRated: (Long, Boolean) -> Unit) {
    var index by remember(phrases) { mutableIntStateOf(0) }; var flipped by remember { mutableStateOf(false) }
    val phrase = phrases.getOrNull(index) ?: return EmptyData("No phrases available for flashcards.")
    Column(Modifier.fillMaxWidth().padding(top = 20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("${stringResource(R.string.card)} ${index + 1}/${phrases.size}", color = Primary, fontWeight = FontWeight.ExtraBold)
        WhiteCard(Modifier.fillMaxWidth().height(320.dp).padding(top = 12.dp).roundClick { flipped = !flipped }) { Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(if (flipped) phrase.meaning else phrase.word, textAlign = TextAlign.Center, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold) } }
        if (flipped) Row(Modifier.fillMaxWidth().padding(top = 12.dp), horizontalArrangement = Arrangement.spacedBy(7.dp)) { listOf("Again" to false, "Hard" to false, "Remembered" to true, "Very easy" to true).forEach { (label, mastered) -> Surface(onClick = { onRated(phrase.id, mastered); flipped = false; index = (index + 1) % phrases.size }, modifier = Modifier.weight(1f).height(58.dp), shape = ControlShape, color = if (mastered) Emerald.copy(.16f) else AmberSoft.copy(alpha = .55f)) { Box(contentAlignment = Alignment.Center) { Text(label, textAlign = TextAlign.Center, fontSize = 10.sp, fontWeight = FontWeight.Bold) } } } }
    }
}

@Composable private fun QuizGame(phrases: List<Vocabulary>, onComplete: () -> Unit) {
    var index by remember { mutableIntStateOf(0) }; var score by remember { mutableIntStateOf(0) }; var result by remember { mutableStateOf<String?>(null) }
    val q = phrases.getOrNull(index)
    if (q == null) { EmptyData("Complete: $score/${phrases.size} correct"); return }
    val options = remember(q.id) { (listOf(q.meaning) + phrases.filter { it.id != q.id }.map { it.meaning }.take(3)).distinct().shuffled() }
    Column(Modifier.fillMaxWidth().padding(top = 20.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp)) {
        WhiteCard(Modifier.fillMaxWidth()) { Column(Modifier.fillMaxWidth().padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) { Text("QUIZ • ${index + 1}/${phrases.size}", color = Primary, fontWeight = FontWeight.ExtraBold); Text(q.word, color = Ink, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 14.dp)) } }
        options.forEach { option -> Surface(onClick = { if (result == null) { val ok = option == q.meaning; if (ok) score++; result = if (ok) "✓ Correct!" else "Answer: ${q.meaning}" } }, modifier = Modifier.fillMaxWidth(), shape = ControlShape, color = SurfaceWhite, border = androidx.compose.foundation.BorderStroke(1.dp, Emerald.copy(alpha = .18f))) { Text(option, Modifier.fillMaxWidth().padding(16.dp), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold) } }
        result?.let { Text(it, color = if (it.startsWith("✓")) Emerald else Error, fontWeight = FontWeight.ExtraBold); Button(onClick = { if (index == phrases.lastIndex) { onComplete(); index++ } else { index++; result = null } }, shape = ControlShape, colors = ButtonDefaults.buttonColors(containerColor = Primary)) { Text(if (index == phrases.lastIndex) "VIEW RESULTS" else "NEXT QUESTION  ›") } }
    }
}
