package com.noor.app.data.memorize

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.memorizeStore by preferencesDataStore(name = "noor_memorize")

data class MemorizeProgress(
    val mode: String = "NEW",
    val surah: String = "",
    val fromPage: String = "1",
    val toPage: String = "1",
    val started: Boolean = false,
    val completedPages: Int = 0
)

/** حفظ جلسة الحفظ محلياً على الجهاز. */
class MemorizeRepository(context: Context) {
    private val store = context.applicationContext.memorizeStore

    val progress: Flow<MemorizeProgress> = store.data.map { p ->
        MemorizeProgress(
            mode = p[MODE] ?: "NEW",
            surah = p[SURAH].orEmpty(),
            fromPage = p[FROM_PAGE] ?: "1",
            toPage = p[TO_PAGE] ?: "1",
            started = p[STARTED] ?: false,
            completedPages = p[COMPLETED] ?: 0
        )
    }

    suspend fun save(value: MemorizeProgress) {
        store.edit { p ->
            p[MODE] = value.mode
            p[SURAH] = value.surah
            p[FROM_PAGE] = value.fromPage
            p[TO_PAGE] = value.toPage
            p[STARTED] = value.started
            p[COMPLETED] = value.completedPages.coerceAtLeast(0)
        }
    }

    private companion object {
        val MODE = stringPreferencesKey("mode")
        val SURAH = stringPreferencesKey("surah")
        val FROM_PAGE = stringPreferencesKey("from_page")
        val TO_PAGE = stringPreferencesKey("to_page")
        val STARTED = booleanPreferencesKey("started")
        val COMPLETED = intPreferencesKey("completed_pages")
    }
}
