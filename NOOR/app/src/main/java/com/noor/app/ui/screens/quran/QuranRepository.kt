package com.noor.app.ui.screens.quran

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

internal data class QuranVerse(
    val surahNumber: Int,
    val verseNumber: Int,
    val text: String
)

internal object QuranRepository {
    private const val SOURCE_URL =
        "https://raw.githubusercontent.com/TarteelAI/quran-assets/main/text/quran-uthmani.txt"
    private const val EXPECTED_VERSES = 6236

    // Verse totals for the 114 surahs; used to reject incomplete or malformed downloads.
    private val verseCounts = intArrayOf(
        7,286,200,176,120,165,206,75,129,109,123,111,43,52,99,128,111,110,98,135,
        112,78,118,64,77,227,93,88,69,60,34,30,73,54,45,83,182,88,75,85,
        54,53,89,59,37,35,38,29,18,45,60,49,62,55,78,96,29,22,24,13,
        14,11,11,18,12,12,30,52,52,44,28,28,20,56,40,31,50,40,46,42,
        29,19,36,25,22,17,19,26,30,20,15,21,11,8,8,19,5,8,8,11,
        11,8,3,9,5,4,7,3,6,3,5,4,5,6
    )

    suspend fun load(context: Context): List<QuranVerse> = withContext(Dispatchers.IO) {
        val cached = File(context.filesDir, "quran-uthmani.txt")
        val fromCache = if (cached.exists()) runCatching { parse(cached) }.getOrNull() else null
        if (fromCache != null) return@withContext fromCache

        cached.delete()
        val connection = (URL(SOURCE_URL).openConnection() as HttpURLConnection).apply {
            connectTimeout = 20_000
            readTimeout = 30_000
            requestMethod = "GET"
            setRequestProperty("User-Agent", "Noor-Android-Quran/1.0")
        }
        try {
            connection.connect()
            if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                error("Quran source returned HTTP ${connection.responseCode}")
            }
            connection.inputStream.use { input ->
                cached.outputStream().use { output -> input.copyTo(output) }
            }
        } finally {
            connection.disconnect()
        }
        try {
            parse(cached)
        } catch (error: Exception) {
            cached.delete()
            throw IllegalStateException("Downloaded Quran text did not pass integrity checks", error)
        }
    }

    private fun parse(file: File): List<QuranVerse> {
        val verses = ArrayList<QuranVerse>(EXPECTED_VERSES)
        file.useLines { lines ->
            lines.forEach { raw ->
                val line = raw.trim()
                if (line.isEmpty() || line.startsWith("#")) return@forEach
                val parts = line.split('|', limit = 3)
                require(parts.size == 3) { "Unexpected Quran text row" }
                val surah = parts[0].toInt()
                val ayah = parts[1].toInt()
                val text = parts[2]
                require(surah in 1..114 && ayah > 0 && text.isNotBlank())
                verses += QuranVerse(surah, ayah, text)
            }
        }
        require(verses.size == EXPECTED_VERSES) { "Expected 6236 verses, got ${verses.size}" }
        val grouped = verses.groupBy { it.surahNumber }
        require(grouped.size == 114)
        verseCounts.forEachIndexed { index, expected ->
            val actual = grouped[index + 1].orEmpty()
            require(actual.size == expected)
            require(actual.map { it.verseNumber } == (1..expected).toList())
        }
        return verses
    }
}
