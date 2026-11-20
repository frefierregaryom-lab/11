package com.noor.app.ui.screens.quran

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import java.text.Normalizer

@Composable
internal fun VoiceTasmiCard(verses: List<QuranVerse>) {
    val context = LocalContext.current
    var index by remember(verses) { mutableStateOf(0) }
    var listening by remember { mutableStateOf(false) }
    var heard by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("اضغط ابدأ ثم اقرأ الآية بصوت واضح") }
    var matchPercent by remember { mutableStateOf<Int?>(null) }
    var recognizer by remember { mutableStateOf<SpeechRecognizer?>(null) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) recognizer?.startListening(Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ar")
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "ar")
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, false)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
        }) else status = "يلزم السماح بالميكروفون لاستخدام التسميع"
    }

    DisposableEffect(context) {
        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            val sr = SpeechRecognizer.createSpeechRecognizer(context)
            sr.setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) { status = "أستمع إليك الآن…" }
                override fun onBeginningOfSpeech() { listening = true }
                override fun onRmsChanged(rmsdB: Float) {}
                override fun onBufferReceived(buffer: ByteArray?) {}
                override fun onEndOfSpeech() { listening = false; status = "جارٍ تحليل ما قُرئ…" }
                override fun onError(error: Int) {
                    listening = false
                    status = when (error) {
                        SpeechRecognizer.ERROR_NO_MATCH -> "لم أتمكن من تمييز التلاوة. حاول مرة أخرى."
                        SpeechRecognizer.ERROR_NETWORK, SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "تعذر الاتصال بخدمة التعرف الصوتي."
                        SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "لا يوجد إذن للميكروفون."
                        else -> "توقف التعرف الصوتي. حاول مجددًا."
                    }
                }
                override fun onResults(results: Bundle?) {
                    listening = false
                    val text = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull().orEmpty()
                    heard = text
                    if (text.isBlank() || verses.getOrNull(index) == null) {
                        status = "لم ألتقط كلمات واضحة."
                    } else {
                        val expected = normalizeArabic(verses[index].text)
                        val actual = normalizeArabic(text)
                        val expectedWords = expected.split(" ").filter { it.isNotBlank() }
                        val actualWords = actual.split(" ").filter { it.isNotBlank() }
                        val matched = expectedWords.zip(actualWords).count { it.first == it.second }
                        matchPercent = if (expectedWords.isEmpty()) 0 else (matched * 100 / expectedWords.size).coerceIn(0, 100)
                        status = "نتيجة آلية تقريبية؛ قد يخطئ التعرف في ألفاظ القرآن."
                    }
                }
                override fun onPartialResults(partialResults: Bundle?) {}
                override fun onEvent(eventType: Int, params: Bundle?) {}
            })
            recognizer = sr
        } else status = "خدمة التعرف الصوتي غير متاحة على هذا الجهاز."
        onDispose {
            recognizer?.cancel()
            recognizer?.destroy()
            recognizer = null
        }
    }

    val current = verses.getOrNull(index)
    androidx.compose.material3.Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("🎙️ التسميع الصوتي", style = MaterialTheme.typography.titleMedium)
            Text("الآية ${current?.verseNumber ?: "—"} من ${verses.size}", style = MaterialTheme.typography.labelMedium)
            Text(current?.text.orEmpty(), style = MaterialTheme.typography.bodyLarge)
            Text(status, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
            if (heard.isNotBlank()) Text("ما تعرّف عليه الهاتف: $heard", style = MaterialTheme.typography.bodySmall)
            matchPercent?.let { Text("التطابق التقريبي: $it%", style = MaterialTheme.typography.titleMedium) }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = {
                        val permission = ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
                        if (permission == PackageManager.PERMISSION_GRANTED) {
                            recognizer?.startListening(Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                                putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ar")
                                putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "ar")
                                putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
                            })
                        } else launcher.launch(Manifest.permission.RECORD_AUDIO)
                    },
                    enabled = !listening && current != null && recognizer != null
                ) { Text(if (listening) "أستمع…" else "ابدأ التسميع") }
                OutlinedButton(onClick = {
                    recognizer?.stopListening()
                    heard = ""
                    matchPercent = null
                    status = "جاهز لإعادة المحاولة"
                }, enabled = listening) { Text("إيقاف") }
                OutlinedButton(onClick = {
                    if (index < verses.lastIndex) index++
                    heard = ""
                    matchPercent = null
                    status = "اقرأ الآية التالية"
                }, enabled = index < verses.lastIndex) { Text("التالية") }
            }
        }
    }
}

private fun normalizeArabic(value: String): String {
    return Normalizer.normalize(value, Normalizer.Form.NFKD)
        .replace(Regex("[\\u064B-\\u065F\\u0670\\u06D6-\\u06ED]"), "")
        .replace("أ", "ا").replace("إ", "ا").replace("آ", "ا")
        .replace("ى", "ي").replace("ؤ", "و").replace("ئ", "ي")
        .replace(Regex("[^\\p{L}\\p{N} ]"), " ")
        .replace(Regex("\\s+"), " ").trim()
}
