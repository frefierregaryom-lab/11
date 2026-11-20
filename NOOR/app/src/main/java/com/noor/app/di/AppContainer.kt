package com.noor.app.di

import android.content.Context
import com.noor.app.data.settings.SettingsRepository

/** حاوية بسيطة للاعتماديات. تُوسَّع في المراحل القادمة (Room، الصلاة، القرآن...). */
class AppContainer(context: Context) {
    val settingsRepository = SettingsRepository(context.applicationContext)
}
