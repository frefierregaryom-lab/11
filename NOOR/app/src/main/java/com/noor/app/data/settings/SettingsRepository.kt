package com.noor.app.data.settings

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.settingsStore by preferencesDataStore(name = "noor_settings")

/** مستودع إعدادات التطبيق. التخزين محلي بالكامل عبر DataStore. */
class SettingsRepository(private val context: Context) {

    val settings: Flow<AppSettings> = context.settingsStore.data
        .catch { e -> if (e is IOException) emit(emptyPreferences()) else throw e }
        .map { it.toSettings() }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.settingsStore.edit { it[KEY_THEME] = mode.name }
    }

    suspend fun setFontScale(scale: Float) {
        context.settingsStore.edit {
            it[KEY_FONT_SCALE] = scale.coerceIn(AppSettings.MIN_FONT_SCALE, AppSettings.MAX_FONT_SCALE)
        }
    }

    suspend fun setUserName(name: String) {
        context.settingsStore.edit {
            it[KEY_NAME] = name.trim().take(AppSettings.MAX_NAME_LENGTH)
        }
    }

    /** حذف كل بيانات الإعدادات. تُوسَّع لاحقاً لتشمل قاعدة البيانات. */
    suspend fun clearAll() {
        context.settingsStore.edit { it.clear() }
    }

    private fun Preferences.toSettings() = AppSettings(
        themeMode = this[KEY_THEME]
            ?.let { runCatching { ThemeMode.valueOf(it) }.getOrNull() }
            ?: ThemeMode.SYSTEM,
        fontScale = (this[KEY_FONT_SCALE] ?: AppSettings.DEFAULT_FONT_SCALE)
            .coerceIn(AppSettings.MIN_FONT_SCALE, AppSettings.MAX_FONT_SCALE),
        userName = this[KEY_NAME].orEmpty()
    )

    private companion object {
        val KEY_THEME = stringPreferencesKey("theme_mode")
        val KEY_FONT_SCALE = floatPreferencesKey("font_scale")
        val KEY_NAME = stringPreferencesKey("user_name")
    }
}
