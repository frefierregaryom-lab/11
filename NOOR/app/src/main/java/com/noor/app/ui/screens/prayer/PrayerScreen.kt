package com.noor.app.ui.screens.prayer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.noor.app.R
import com.noor.app.domain.ArabicDates
import com.noor.app.domain.PrayerTimesCalculator
import com.noor.app.ui.components.NoorCard
import com.noor.app.ui.components.ScreenScaffold
import com.noor.app.ui.components.SectionTitle
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun PrayerScreen() {
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    val times = remember(selectedDate) { PrayerTimesCalculator.calculate(selectedDate) }
    val today = selectedDate == LocalDate.now()
    val now = LocalTime.now()
    val prayers = listOf(
        Triple("الفجر", times.fajr, "🌅"),
        Triple("الظهر", times.dhuhr, "☀️"),
        Triple("العصر", times.asr, "🌤️"),
        Triple("المغرب", times.maghrib, "🌇"),
        Triple("العشاء", times.isha, "🌙")
    )
    val nextPrayer = if (today) prayers.firstOrNull { it.second.isAfter(now) } else null

    ScreenScaffold(title = stringResource(R.string.section_prayer), hasBottomBar = true) { padding ->
        Column(
            modifier = Modifier.padding(padding).verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            NoorCard(Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("مواقيت الصلاة", style = MaterialTheme.typography.titleLarge)
                    Text("الموقع المعتمد: غزة، فلسطين", style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Row(verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        TextButton(onClick = { selectedDate = selectedDate.minusDays(1) }) { Text("اليوم السابق") }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(ArabicDates.weekdayName(selectedDate), style = MaterialTheme.typography.titleMedium)
                            Text(ArabicDates.gregorian(selectedDate), style = MaterialTheme.typography.bodySmall)
                        }
                        TextButton(onClick = { selectedDate = selectedDate.plusDays(1) },
                            enabled = selectedDate.isBefore(LocalDate.now().plusDays(30))) { Text("اليوم التالي") }
                    }
                    if (!today) TextButton(onClick = { selectedDate = LocalDate.now() }) { Text("العودة إلى اليوم") }
                }
            }
            if (today && nextPrayer != null) {
                NoorCard(Modifier.fillMaxWidth()) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("الصلاة القادمة", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                        Text(nextPrayer.first, style = MaterialTheme.typography.headlineMedium)
                        Text("موعدها " + formatTime(nextPrayer.second), style = MaterialTheme.typography.bodyLarge)
                    }
                }
            } else if (today) {
                NoorCard(Modifier.fillMaxWidth()) { Text("انتهت مواقيت صلوات اليوم. نسأل الله القبول.") }
            }
            SectionTitle("مواقيت الصلاة")
            NoorCard(Modifier.fillMaxWidth()) {
                Column {
                    prayers.forEachIndexed { index, item ->
                        val isNext = today && nextPrayer == item
                        Row(Modifier.fillMaxWidth().padding(vertical = 13.dp, horizontal = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween) {
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text(item.third, style = MaterialTheme.typography.titleLarge)
                                Text(item.first, style = MaterialTheme.typography.titleMedium,
                                    color = if (isNext) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface)
                            }
                            Text(formatTime(item.second), style = MaterialTheme.typography.titleMedium,
                                color = if (isNext) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface)
                        }
                        if (index != prayers.lastIndex) HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    Row(Modifier.fillMaxWidth().padding(vertical = 13.dp, horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("الشروق", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(formatTime(times.sunrise), style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
            Text("طريقة الحساب: رابطة العالم الإسلامي، والمذهب الشافعي للعصر. قد تختلف المواقيت دقائق عن التقويم المحلي المعتمد؛ يُرجى مطابقتها مع مسجدك.",
                style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

private fun formatTime(time: LocalTime): String {
    val value = time.format(DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH))
    return ArabicDates.toArabicDigits(value.replace("AM", "ص").replace("PM", "م"))
}
