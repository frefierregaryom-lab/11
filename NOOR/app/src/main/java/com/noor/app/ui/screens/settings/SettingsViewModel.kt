package com.noor.app.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.noor.app.data.settings.AppSettings
import com.noor.app.data.settings.SettingsRepository
import com.noor.app.data.settings.ThemeMode
import com.noor.app.di.AppContainer
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(private val repository: SettingsRepository) : ViewModel() {

    val settings: StateFlow<AppSettings> = repository.settings
        .stateIn(viewModelScope, SharingStarted.Eagerly, AppSettings())

    fun setThemeMode(mode: ThemeMode) = viewModelScope.launch { repository.setThemeMode(mode) }
    fun setFontScale(scale: Float) = viewModelScope.launch { repository.setFontScale(scale) }
    fun setUserName(name: String) = viewModelScope.launch { repository.setUserName(name) }
    fun clearAll() = viewModelScope.launch { repository.clearAll() }

    class Factory(private val container: AppContainer) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            require(modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
                "ViewModel غير معروف: ${modelClass.name}"
            }
            return SettingsViewModel(container.settingsRepository) as T
        }
    }
}
