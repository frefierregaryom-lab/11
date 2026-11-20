package com.noor.app.data.memorize

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

private val Context.memorizeStore by preferencesDataStore(name = "noor_memorize")

data class MemorizeProgress(
    val mode: String = "NEW",
    val surah: String = "",
    val fromPage: String = "1",
    val toPage: String = "1",
    val started: Boolean = false,
    val completedPages: Int = 0,
    val dailyGoal: Int = 2,
    val dailyCompleted: Int = 0,
    val dailyDate: String = LocalDate.now().toString()
)

/** حفظ جلسة الحفظ والهدف اليومي محلياً على الجهاز. */
class MemorizeRepository(context: Context) {
    private val store = context.applicationContext.memorizeStore

    val progress: Flow<MemorizeProgress> = store.data.map { p ->
        val today = LocalDate.now().toString()
        val savedDate = p[DAILY_DATE] ?: today
        MemorizeProgress(
            mode = p[MODE] ?: "NEW",
            surah = p[SURAH].orEmpty(),
            fromPage = p[FROM_PAGE] ?: "1",
            toPage = p[TO_PAGE] ?: "1",
            started = p[STARTED] ?: false,
            completedPages = p[COMPLETED] ?: 0,
            dailyGoal = (p[DAILY_GOAL] ?: 2).coerceIn(1, 20),
            dailyCompleted = if (savedDate == today) (p[DAILY_COMPLETED] ?: 0) else 0,
            dailyDate = today
        )
    }

    suspend fun save(value: MemorizeProgress) {
        store.edit { p ->
            val today = LocalDate.now().toString()
            val sameDay = (p[DAILY_DATE] ?: today) == today
            p[MODE] = value.mode
            p[SURAH] = value.surah
            p[FROM_PAGE] = value.fromPage
            p[TO_PAGE] = value.toPage
            p[STARTED] = value.started
            p[COMPLETED] = value.completedPages.coerceAtLeast(0)
            p[DAILY_GOAL] = value.dailyGoal.coerceIn(1, 20)
            p[DAILY_COMPLETED] = if (sameDay) value.dailyCompleted.coerceAtLeast(0) else 0
            p[DAILY_DATE] = today
        }
    }

    private companion object {
        val MODE = stringPreferencesKey("mode")
        val SURAH = stringPreferencesKey("surah")
        val FROM_PAGE = stringPreferencesKey("from_page")
        val TO_PAGE = stringPreferencesKey("to_page")
        val STARTED = booleanPreferencesKey("started")
        val COMPLETED = intPreferencesKey("completed_pages")
        val DAILY_GOAL = intPreferencesKey("daily_goal")
        val DAILY_COMPLETED = intPreferencesKey("daily_completed")
        val DAILY_DATE = stringPreferencesKey("daily_date")
    }
}
