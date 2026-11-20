package com.noor.app.ui.screens.calendar

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.noor.app.domain.ArabicDates
import com.noor.app.ui.components.NoorCard
import com.noor.app.ui.components.ScreenScaffold
import java.time.LocalDate
import java.time.YearMonth
import java.time.chrono.HijrahChronology
import java.time.temporal.ChronoField

private val monthNames = listOf("يناير","فبراير","مارس","أبريل","مايو","يونيو","يوليو","أغسطس","سبتمبر","أكتوبر","نوفمبر","ديسمبر")
private val weekNames = listOf("س","ح","ن","ث","ر","خ","ج")
private data class Occasion(val day:Int,val month:Int,val name:String,val note:String)
private val occasions = listOf(
    Occasion(1,1,"رأس السنة الهجرية","بداية شهر محرّم"),
    Occasion(10,1,"يوم عاشوراء","العاشر من محرّم"),
    Occasion(12,3,"المولد النبوي","١٢ ربيع الأول — وفق التقويم المعتمد"),
    Occasion(27,7,"الإسراء والمعراج","٢٧ رجب — التاريخ تقديري"),
    Occasion(1,9,"بداية رمضان","١ رمضان — يثبت بالرؤية"),
    Occasion(17,9,"غزوة بدر","١٧ رمضان"),
    Occasion(27,9,"ليلة القدر (تحرّي)","ليلة مرجوة ضمن العشر الأواخر"),
    Occasion(1,10,"عيد الفطر","١ شوّال — يثبت بالرؤية"),
    Occasion(9,12,"يوم عرفة","٩ ذو الحجة — يثبت بالرؤية"),
    Occasion(10,12,"عيد الأضحى","١٠ ذو الحجة — يثبت بالرؤية")
)

@Composable
fun CalendarScreen() {
    var month by remember { mutableStateOf(YearMonth.now()) }
    var selected by remember { mutableStateOf(LocalDate.now()) }
    val firstOffset = month.atDay(1).dayOfWeek.value % 7
    val days = (0 until firstOffset).map { null } + (1..month.lengthOfMonth()).map { it }
    val hijri = HijrahChronology.INSTANCE.date(selected)
    val selectedOccasion = occasions.filter {
        it.day == hijri.get(ChronoField.DAY_OF_MONTH) && it.month == hijri.get(ChronoField.MONTH_OF_YEAR)
    }
    ScreenScaffold(title = "التقويم والمناسبات", hasBottomBar = false) { padding ->
        Column(Modifier.padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            NoorCard(Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(ArabicDates.hijri(selected), style = MaterialTheme.typography.titleLarge)
                    Text(ArabicDates.gregorian(selected), style = MaterialTheme.typography.bodyMedium)
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        TextButton(onClick = { month = month.minusMonths(1) }) { Text("السابق") }
                        Text(monthNames[month.monthValue-1] + " " + ArabicDates.toArabicDigits(month.year.toString()), style = MaterialTheme.typography.titleMedium)
                        TextButton(onClick = { month = month.plusMonths(1) }) { Text("التالي") }
                    }
                    LazyVerticalGrid(columns = GridCells.Fixed(7), modifier = Modifier.fillMaxWidth()) {
                        items(weekNames) { Text(it, modifier = Modifier.padding(5.dp), textAlign = TextAlign.Center, style = MaterialTheme.typography.labelMedium) }
                        items(days) { day ->
                            if (day == null) Text("")
                            else {
                                val date = month.atDay(day)
                                val chosen = date == selected
                                Text(
                                    text = ArabicDates.toArabicDigits(day.toString()) + if (isOccasion(date)) " •" else "",
                                    modifier = Modifier.fillMaxWidth().clickable { selected = date }.padding(vertical = 12.dp),
                                    textAlign = TextAlign.Center,
                                    color = if (chosen) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                    style = if (chosen) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }
            Text("المناسبات الإسلامية", style = MaterialTheme.typography.titleLarge)
            NoorCard(Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    if (selectedOccasion.isEmpty()) {
                        Text("لا توجد مناسبة مدرجة لهذا اليوم. قد تختلف التواريخ يومًا حسب رؤية الهلال.")
                    } else selectedOccasion.forEach {
                        Text(it.name, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                        Text(it.note, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
            Text("التواريخ الهجرية تقديرية وفق تقويم أم القرى، وقد تختلف بداية الأشهر والمناسبات حسب ثبوت الهلال والإعلان الرسمي.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

private fun isOccasion(date: LocalDate): Boolean {
    val h = HijrahChronology.INSTANCE.date(date)
    return occasions.any { it.day == h.get(ChronoField.DAY_OF_MONTH) && it.month == h.get(ChronoField.MONTH_OF_YEAR) }
}
