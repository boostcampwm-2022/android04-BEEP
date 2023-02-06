package com.lighthouse

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.lighthouse.presentation.background.BeepWorkManager
import com.lighthouse.utils.log.ComponentLogger
import com.lighthouse.utils.log.CustomTimberTree
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class BeepApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var componentLogger: ComponentLogger

    @Inject
    lateinit var customTimberTree: CustomTimberTree

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()
        Timber.plant(customTimberTree)
        componentLogger.initialize(this)

        BeepWorkManager(this)
    }

    override fun getWorkManagerConfiguration() =
        Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}


