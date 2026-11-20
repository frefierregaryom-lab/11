package com.noor.app.ui.screens.quran

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
    var selectedPage by remember { mutableStateOf<Int?>(null) }
    var showPages by remember { mutableStateOf(false) }
    var verses by remember { mutableStateOf<List<QuranVerse>>(emptyList()) }
    var pageVerses by remember { mutableStateOf<List<QuranPageVerse>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var pageLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf(false) }
    var pageError by remember { mutableStateOf(false) }
    var retry by remember { mutableStateOf(0) }
    var pageRetry by remember { mutableStateOf(0) }

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

    LaunchedEffect(selectedPage, pageRetry) {
        val page = selectedPage ?: return@LaunchedEffect
        pageLoading = true
        pageError = false
        try {
            pageVerses = QuranPageRepository.loadPage(context, page)
        } catch (_: Exception) {
            pageError = true
        } finally {
            pageLoading = false
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
    val verseLookup = remember(verses) {
        verses.associateBy { it.surahNumber to it.verseNumber }
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
            if (selectedSurah == null && selectedPage == null) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (!showPages) {
                        Button(onClick = { showPages = false }) {
                            Text(stringResource(R.string.quran_surahs_tab))
                        }
                        OutlinedButton(onClick = { showPages = true }) {
                            Text(stringResource(R.string.quran_pages_tab))
                        }
                    } else {
                        OutlinedButton(onClick = { showPages = false }) {
                            Text(stringResource(R.string.quran_surahs_tab))
                        }
                        Button(onClick = { showPages = true }) {
                            Text(stringResource(R.string.quran_pages_tab))
                        }
                    }
                }
                if (!showPages) {
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
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedSurah = surah },
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
                    Text(stringResource(R.string.quran_pages_intro))
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items((1..604).toList(), key = { it }) { page ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedPage = page },
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                )
                            ) {
                                Text(
                                    text = stringResource(R.string.quran_page_number, page),
                                    modifier = Modifier.padding(14.dp),
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                        }
                    }
                }
            } else if (selectedSurah != null) {
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
                                VerseCard(verse.text, verse.verseNumber.toString())
                            }
                        }
                    }
                }
            } else {
                val page = selectedPage!!
                TextButton(onClick = { selectedPage = null }) {
                    Text(stringResource(R.string.quran_back_to_pages))
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(
                        onClick = { if (page > 1) selectedPage = page - 1 },
                        enabled = page > 1
                    ) { Text(stringResource(R.string.quran_previous_page)) }
                    Text(
                        text = stringResource(R.string.quran_page_number, page),
                        style = MaterialTheme.typography.titleMedium
                    )
                    TextButton(
                        onClick = { if (page < 604) selectedPage = page + 1 },
                        enabled = page < 604
                    ) { Text(stringResource(R.string.quran_next_page)) }
                }
                when {
                    loading -> {
                        CircularProgressIndicator()
                        Text(stringResource(R.string.quran_loading))
                    }
                    pageLoading -> {
                        CircularProgressIndicator()
                        Text(stringResource(R.string.quran_page_loading))
                    }
                    pageError -> {
                        Text(stringResource(R.string.quran_page_error))
                        TextButton(onClick = { pageRetry++ }) {
                            Text(stringResource(R.string.quran_retry))
                        }
                    }
                    error -> {
                        Text(stringResource(R.string.quran_load_error))
                        TextButton(onClick = { retry++ }) {
                            Text(stringResource(R.string.quran_retry))
                        }
                    }
                    else -> {
                        val pageText = pageVerses.mapNotNull { reference ->
                            verseLookup[reference.surahNumber to reference.verseNumber]?.let { verse ->
                                verse.text + " ۞"
                            }
                        }.joinToString("  ")
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .pointerInput(page) {
                                    var drag = 0f
                                    detectHorizontalDragGestures(
                                        onHorizontalDrag = { _, amount -> drag += amount },
                                        onDragEnd = {
                                            if (drag > 70f && page > 1) selectedPage = page - 1
                                            else if (drag < -70f && page < 604) selectedPage = page + 1
                                            drag = 0f
                                        }
                                    )
                                },
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(
                                Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = pageText,
                                    modifier = Modifier.fillMaxWidth(),
                                    style = MaterialTheme.typography.headlineSmall,
                                    textAlign = TextAlign.Justify,
                                    lineHeight = MaterialTheme.typography.headlineSmall.lineHeight * 1.8
                                )
                            }
                        }
                        Text(
                            "اسحب يمينًا أو يسارًا لتقليب الصفحة",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
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

@Composable
private fun VerseCard(text: String, label: String) {
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
                text = text,
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.End
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
