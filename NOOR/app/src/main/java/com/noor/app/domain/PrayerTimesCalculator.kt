
package com.noor.app.domain

import com.batoulapps.adhan.CalculationMethod
import com.batoulapps.adhan.Coordinates
import com.batoulapps.adhan.Madhab
import com.batoulapps.adhan.PrayerTimes
import com.batoulapps.adhan.data.DateComponents
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.Instant
import java.util.Date

data class DailyPrayerTimes(
    val fajr: LocalTime,
    val sunrise: LocalTime,
    val dhuhr: LocalTime,
    val asr: LocalTime,
    val maghrib: LocalTime,
    val isha: LocalTime
)

object PrayerTimesCalculator {

    const val DEFAULT_CITY = "غزة"

    private const val LATITUDE = 31.5017
    private const val LONGITUDE = 34.4668

    private val palestineZone = ZoneId.of("Asia/Gaza")

    private val coordinates = Coordinates(
        LATITUDE,
        LONGITUDE
    )

    fun calculate(
        date: LocalDate = LocalDate.now()
    ): DailyPrayerTimes {

        val dateComponents = DateComponents(
            date.year,
            date.monthValue,
            date.dayOfMonth
        )

        val parameters =
            CalculationMethod.MUSLIM_WORLD_LEAGUE.parameters

        parameters.madhab = Madhab.SHAFI

        val prayerTimes = PrayerTimes(
            coordinates,
            dateComponents,
            parameters
        )

        return DailyPrayerTimes(
            fajr = toLocalTime(prayerTimes.fajr),
            sunrise = toLocalTime(prayerTimes.sunrise),
            dhuhr = toLocalTime(prayerTimes.dhuhr),
            asr = toLocalTime(prayerTimes.asr),
            maghrib = toLocalTime(prayerTimes.maghrib),
            isha = toLocalTime(prayerTimes.isha)
        )
    }

    private fun toLocalTime(date: Date): LocalTime {
        return Instant.ofEpochMilli(date.time)
            .atZone(palestineZone)
            .toLocalTime()
    }
}
