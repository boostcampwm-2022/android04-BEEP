package com.lighthouse

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class BeepApplication : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}
