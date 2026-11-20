package com.noor.app

import android.app.Application
import com.noor.app.di.AppContainer

class NoorApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
