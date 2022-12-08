package com.lighthouse

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.lighthouse.beep.BuildConfig
import com.lighthouse.presentation.background.BeepWorkManager
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class BeepApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(CustomTimberTree())
        }

        BeepWorkManager(this)
    }

    override fun getWorkManagerConfiguration() =
        Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}

class CustomTimberTree : Timber.DebugTree() {
    override fun createStackElementTag(element: StackTraceElement): String {
        return "${element.className}:${element.lineNumber}#${element.methodName}"
    }
}
