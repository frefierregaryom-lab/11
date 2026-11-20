package com.noor.app.domain

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test

class ArabicDatesTest {

    @Test
    fun digitsAreConvertedToArabicIndic() {
        assertEquals("٠١٢٣٤٥٦٧٨٩", ArabicDates.toArabicDigits("0123456789"))
        assertEquals("المرحلة ٥", ArabicDates.toArabicDigits("المرحلة 5"))
    }

    @Test
    fun hijriFirstOfRamadan1446() {
        // 1 رمضان 1446 = 1 مارس 2025 (تقويم أم القرى)
        assertEquals("١ رمضان ١٤٤٦ هـ", ArabicDates.hijri(LocalDate.of(2025, 3, 1)))
    }

    @Test
    fun hijriFirstOfMuharram1447() {
        assertEquals("١ محرّم ١٤٤٧ هـ", ArabicDates.hijri(LocalDate.of(2025, 6, 26)))
    }

    @Test
    fun gregorianFormat() {
        assertEquals("١ مارس ٢٠٢٥", ArabicDates.gregorian(LocalDate.of(2025, 3, 1)))
    }

    @Test
    fun weekdayName() {
        // 1 مارس 2025 كان يوم السبت
        assertEquals("السبت", ArabicDates.weekdayName(LocalDate.of(2025, 3, 1)))
    }

    @Test
    fun dayPartBoundaries() {
        assertEquals(ArabicDates.DayPart.MORNING, ArabicDates.dayPart(4))
        assertEquals(ArabicDates.DayPart.MORNING, ArabicDates.dayPart(11))
        assertEquals(ArabicDates.DayPart.EVENING, ArabicDates.dayPart(12))
        assertEquals(ArabicDates.DayPart.EVENING, ArabicDates.dayPart(3))
    }
}
