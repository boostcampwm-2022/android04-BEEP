package com.lighthouse.presentation.background

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit

class BeepWorkManager(@ApplicationContext context: Context) {
    private val notificationWorkRequest: PeriodicWorkRequest =
        PeriodicWorkRequestBuilder<NotificationWorker>(NOTIFICATION_INTERVAL, TimeUnit.MINUTES).build()

    init {
        WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork(WORK_NAME, ExistingPeriodicWorkPolicy.KEEP, notificationWorkRequest)
    }

    companion object {
        private const val WORK_NAME = "notification"
        private const val NOTIFICATION_INTERVAL = 15L // test를 위해 최소 간격인 15분으로 설정. 원래는 24시간.
    }
}
