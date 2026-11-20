package com.noor.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.noor.app.ui.navigation.NoorApp
import com.noor.app.ui.screens.settings.SettingsViewModel
import com.noor.app.ui.theme.NoorTheme

class MainActivity : ComponentActivity() {

    private val settingsViewModel: SettingsViewModel by viewModels {
        SettingsViewModel.Factory((application as NoorApplication).container)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val settings by settingsViewModel.settings.collectAsStateWithLifecycle()
            NoorTheme(themeMode = settings.themeMode, fontScale = settings.fontScale) {
                NoorApp(settingsViewModel)
            }
        }
    }
}
