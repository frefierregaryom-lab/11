package com.noor.app.ui.screens.quran

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

internal data class QuranPageVerse(
    val surahNumber: Int,
    val surahName: String,
    val verseNumber: Int
)

internal object QuranPageRepository {
    private const val PAGE_COUNT = 604
    private const val API = "https://api.alquran.cloud/v1/page"

    suspend fun loadPage(context: Context, page: Int): List<QuranPageVerse> =
        withContext(Dispatchers.IO) {
            require(page in 1..PAGE_COUNT)
            val cache = File(context.filesDir, "quran-page-$page.json")
            val cached = if (cache.exists()) runCatching { parse(cache.readText(), page) }.getOrNull() else null
            if (cached != null) return@withContext cached

            cache.delete()
            val connection = (URL("$API/$page/quran-uthmani").openConnection() as HttpURLConnection).apply {
                connectTimeout = 15_000
                readTimeout = 20_000
                requestMethod = "GET"
                setRequestProperty("User-Agent", "Noor-Android-Quran/1.0")
            }
            val response = try {
                connection.connect()
                require(connection.responseCode == HttpURLConnection.HTTP_OK) {
                    "Page service returned HTTP ${connection.responseCode}"
                }
                connection.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
            } finally {
                connection.disconnect()
            }

            val result = parse(response, page)
            cache.writeText(response, Charsets.UTF_8)
            result
        }

    private fun parse(json: String, requestedPage: Int): List<QuranPageVerse> {
        val root = JSONObject(json)
        require(root.optString("code") == "200") { "Page service response was not successful" }
        val ayahs = root.getJSONObject("data").getJSONArray("ayahs")
        require(ayahs.length() in 1..20) { "Unexpected number of ayahs on page" }
        return buildList {
            for (index in 0 until ayahs.length()) {
                val ayah = ayahs.getJSONObject(index)
                require(ayah.optInt("page") == requestedPage)
                val surah = ayah.getJSONObject("surah")
                val surahNumber = surah.getInt("number")
                val verseNumber = ayah.getInt("numberInSurah")
                val surahName = surah.getString("name")
                require(surahNumber in 1..114 && verseNumber > 0 && surahName.isNotBlank())
                add(QuranPageVerse(surahNumber, surahName, verseNumber))
            }
        }
    }
}
