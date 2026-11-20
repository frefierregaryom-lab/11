package com.noor.app.domain

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.chrono.HijrahChronology
import java.time.temporal.ChronoField

/** تنسيق التواريخ بالعربية (هجري بتقويم أم القرى، وميلادي) بأرقام هندية. */
object ArabicDates {

    private val gregorianMonths = arrayOf(
        "يناير", "فبراير", "مارس", "أبريل", "مايو", "يونيو",
        "يوليو", "أغسطس", "سبتمبر", "أكتوبر", "نوفمبر", "ديسمبر"
    )

    private val hijriMonths = arrayOf(
        "محرّم", "صفر", "ربيع الأول", "ربيع الآخر", "جمادى الأولى", "جمادى الآخرة",
        "رجب", "شعبان", "رمضان", "شوّال", "ذو القعدة", "ذو الحجة"
    )

    fun weekdayName(date: LocalDate): String = when (date.dayOfWeek) {
        DayOfWeek.SATURDAY -> "السبت"
        DayOfWeek.SUNDAY -> "الأحد"
        DayOfWeek.MONDAY -> "الاثنين"
        DayOfWeek.TUESDAY -> "الثلاثاء"
        DayOfWeek.WEDNESDAY -> "الأربعاء"
        DayOfWeek.THURSDAY -> "الخميس"
        DayOfWeek.FRIDAY -> "الجمعة"
    }

    fun toArabicDigits(text: String): String = buildString(text.length) {
        for (c in text) append(if (c in '0'..'9') '٠' + (c - '0') else c)
    }

    fun gregorian(date: LocalDate): String =
        toArabicDigits("${date.dayOfMonth}") + " " +
            gregorianMonths[date.monthValue - 1] + " " +
            toArabicDigits("${date.year}")

    /** مثال: «١ رمضان ١٤٤٦ هـ». */
    fun hijri(date: LocalDate, suffix: String = "هـ"): String {
        val h = HijrahChronology.INSTANCE.date(date)
        val day = h.get(ChronoField.DAY_OF_MONTH)
        val month = h.get(ChronoField.MONTH_OF_YEAR)
        val year = h.get(ChronoField.YEAR)
        return toArabicDigits("$day") + " " + hijriMonths[month - 1] + " " +
            toArabicDigits("$year") + " " + suffix
    }

    enum class DayPart { MORNING, EVENING }

    fun dayPart(hour: Int): DayPart = if (hour in 4..11) DayPart.MORNING else DayPart.EVENING
}
