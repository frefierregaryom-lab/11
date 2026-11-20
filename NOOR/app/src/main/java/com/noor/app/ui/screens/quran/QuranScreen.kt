package com.noor.app.ui.screens.quran

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.noor.app.R
import com.noor.app.ui.components.ScreenScaffold

private data class SurahItem(val number: Int, val name: String)

private val surahs = listOf(
    "الفاتحة", "البقرة", "آل عمران", "النساء", "المائدة", "الأنعام", "الأعراف", "الأنفال",
    "التوبة", "يونس", "هود", "يوسف", "الرعد", "إبراهيم", "الحجر", "النحل",
    "الإسراء", "الكهف", "مريم", "طه", "الأنبياء", "الحج", "المؤمنون", "النور",
    "الفرقان", "الشعراء", "النمل", "القصص", "العنكبوت", "الروم", "لقمان", "السجدة",
    "الأحزاب", "سبأ", "فاطر", "يس", "الصافات", "ص", "الزمر", "غافر",
    "فصلت", "الشورى", "الزخرف", "الدخان", "الجاثية", "الأحقاف", "محمد", "الفتح",
    "الحجرات", "ق", "الذاريات", "الطور", "النجم", "القمر", "الرحمن", "الواقعة",
    "الحديد", "المجادلة", "الحشر", "الممتحنة", "الصف", "الجمعة", "المنافقون", "التغابن",
    "الطلاق", "التحريم", "الملك", "القلم", "الحاقة", "المعارج", "نوح", "الجن",
    "المزمل", "المدثر", "القيامة", "الإنسان", "المرسلات", "النبأ", "النازعات", "عبس",
    "التكوير", "الانفطار", "المطففين", "الانشقاق", "البروج", "الطارق", "الأعلى", "الغاشية",
    "الفجر", "البلد", "الشمس", "الليل", "الضحى", "الشرح", "التين", "العلق",
    "القدر", "البينة", "الزلزلة", "العاديات", "القارعة", "التكاثر", "العصر", "الهمزة",
    "الفيل", "قريش", "الماعون", "الكوثر", "الكافرون", "النصر", "المسد", "الإخلاص",
    "الفلق", "الناس"
).mapIndexed { index, name -> SurahItem(index + 1, name) }

@Composable
fun QuranScreen() {
    val context = LocalContext.current.applicationContext
    var query by remember { mutableStateOf("") }
    var selectedSurah by remember { mutableStateOf<SurahItem?>(null) }
    var verses by remember { mutableStateOf<List<QuranVerse>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf(false) }
    var retry by remember { mutableStateOf(0) }

    LaunchedEffect(retry) {
        loading = true
        error = false
        try {
            verses = QuranRepository.load(context)
        } catch (_: Exception) {
            error = true
        } finally {
            loading = false
        }
    }

    val filtered = remember(query) {
        surahs.filter {
            it.name.contains(query.trim(), ignoreCase = true) ||
                it.number.toString() == query.trim()
        }
    }
    val selectedVerses = remember(verses, selectedSurah) {
        verses.filter { it.surahNumber == selectedSurah?.number }
    }

    ScreenScaffold(
        title = stringResource(R.string.section_quran),
        onBack = null,
        hasBottomBar = true
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (selectedSurah == null) {
                Text(
                    text = stringResource(R.string.quran_directory_intro),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(stringResource(R.string.quran_search_hint)) },
                    singleLine = true
                )
                Text(
                    text = stringResource(R.string.quran_surah_count, filtered.size),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filtered, key = { it.number }) { surah ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { selectedSurah = surah },
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                horizontalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                Text(
                                    text = surah.number.toString(),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(surah.name, style = MaterialTheme.typography.titleMedium)
                            }
                        }
                    }
                }
            } else {
                TextButton(onClick = { selectedSurah = null }) {
                    Text(stringResource(R.string.quran_back_to_surahs))
                }
                Text(
                    text = stringResource(
                        R.string.quran_selected_surah,
                        selectedSurah!!.number,
                        selectedSurah!!.name
                    ),
                    style = MaterialTheme.typography.headlineSmall
                )
                when {
                    loading -> {
                        CircularProgressIndicator()
                        Text(stringResource(R.string.quran_loading))
                    }
                    error -> {
                        Text(stringResource(R.string.quran_load_error))
                        TextButton(onClick = { retry++ }) {
                            Text(stringResource(R.string.quran_retry))
                        }
                    }
                    else -> {
                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(selectedVerses, key = { it.verseNumber }) { verse ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surface
                                    )
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text(
                                            text = verse.text,
                                            modifier = Modifier.fillMaxWidth(),
                                            style = MaterialTheme.typography.headlineSmall,
                                            textAlign = TextAlign.End
                                        )
                                        Text(
                                            text = verse.verseNumber.toString(),
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
            Text(
                text = stringResource(R.string.quran_source_notice),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
