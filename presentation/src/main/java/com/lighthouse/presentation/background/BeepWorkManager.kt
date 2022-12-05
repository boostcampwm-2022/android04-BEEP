package com.lighthouse.presentation.background

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.lighthouse.presentation.ui.widget.BeepWidgetWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit

class BeepWorkManager(@ApplicationContext context: Context) {

    private val notificationWorkRequest: PeriodicWorkRequest =
        PeriodicWorkRequestBuilder<NotificationWorker>(NOTIFICATION_INTERVAL, TimeUnit.MINUTES).build()

    private val widgetWorkRequest: OneTimeWorkRequest =
        OneTimeWorkRequestBuilder<BeepWidgetWorker>().build()

    init {
        WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork(NOTIFICATION_WORK_NAME, ExistingPeriodicWorkPolicy.KEEP, notificationWorkRequest)

        WorkManager.getInstance(context)
            .enqueueUniqueWork(WIDGET_WORK_NAME, ExistingWorkPolicy.KEEP, widgetWorkRequest)
    }

    companion object {
        private const val NOTIFICATION_WORK_NAME = "notification"
        private const val WIDGET_WORK_NAME = "widget"
        private const val NOTIFICATION_INTERVAL = 15L // test를 위해 최소 간격인 15분으로 설정. 원래는 24시간.
    }
}
