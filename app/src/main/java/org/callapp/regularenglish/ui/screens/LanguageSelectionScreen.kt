package org.callapp.regularenglish.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LanguageSelectionScreen(onSelect: (String) -> Unit) {
    val languages = listOf(
        LanguageChoice("vi", "🇻🇳", "Vietnam", "Tiếng Việt"),
        LanguageChoice("fr", "🇫🇷", "France", "Français"),
        LanguageChoice("pt", "🇵🇹", "Portugal", "Português"),
        LanguageChoice("ja", "🇯🇵", "Japan", "日本語"),
        LanguageChoice("ko", "🇰🇷", "South Korea", "한국어"),
        LanguageChoice("ru", "🇷🇺", "Russia", "Русский"),
        LanguageChoice("zh", "🇨🇳", "China", "中文"),
        LanguageChoice("de", "🇩🇪", "Germany", "Deutsch"),
        LanguageChoice("es", "🇪🇸", "Spain", "Español")
    )
    var selected by remember { mutableStateOf(languages.first()) }
    AppPage(showBanner = false) { padding -> Box(Modifier.fillMaxSize().padding(padding).padding(horizontal = 24.dp), contentAlignment = Alignment.Center) {
        WhiteCard(Modifier.fillMaxWidth().widthIn(max = 420.dp)) { Column(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 26.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(Modifier.size(72.dp).clip(ControlShape).background(Emerald.copy(alpha = .16f)), contentAlignment = Alignment.Center) { Text("A", color = Primary, fontWeight = FontWeight.ExtraBold, fontSize = 32.sp) }
            Text("Choose your native language", color = Ink, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 18.dp))
            Text("Lesson translations will use this language.", color = MutedInk, fontSize = 14.sp, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 8.dp))
            Spacer(Modifier.height(22.dp))
            languages.chunked(3).forEach { row ->
                Row(Modifier.fillMaxWidth().padding(bottom = 9.dp), horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                    row.forEach { language ->
                        val active = language == selected
                        // A plain Box deliberately avoids Material Surface's pressed-state layer.
                        Box(
                            Modifier.weight(1f).height(92.dp)
                                .clip(ControlShape)
                                .background(if (active) Emerald.copy(alpha = .17f) else Mint)
                                .border(1.dp, if (active) Emerald.copy(alpha = .72f) else Emerald.copy(alpha = .24f), ControlShape)
                                .roundClick { selected = language },
                            contentAlignment = Alignment.Center
                        ) { Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                            Text(language.flag, fontSize = 24.sp)
                            Text(language.country, color = Ink, fontSize = 11.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 4.dp))
                            Text(language.nativeName, color = if (active) TealDark else MutedInk, fontSize = 10.sp, textAlign = TextAlign.Center, maxLines = 1)
                        } }
                    }
                }
            }
            Button(onClick = { onSelect(selected.code) }, modifier = Modifier.fillMaxWidth().padding(top = 10.dp).height(54.dp), shape = ControlShape, colors = ButtonDefaults.buttonColors(containerColor = Primary, contentColor = SurfaceWhite)) { Text("NEXT  →", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp) }
        } }
    } }
}

private data class LanguageChoice(val code: String, val flag: String, val country: String, val nativeName: String)
