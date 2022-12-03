package com.lighthouse.presentation.notification

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import java.util.concurrent.TimeUnit

@HiltWorker
class NotificationWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val notificationHelper: NotificationHelper
) :
    Worker(context, workerParams) {

    override fun doWork(): Result {
        createNotification()
        Timber.tag("SOOJIN").d("WORK")

        return Result.success()
    }

    private fun createNotification() {
        val list = listOf("첫번째", "두번재", "세번재", "네번째")
        list.indices.forEach { notificationHelper.applyNotification(list[it], it) }
    }
}

class NotificationWorkManager(@ApplicationContext context: Context) {
    private val notificationWorkRequest: PeriodicWorkRequest =
        PeriodicWorkRequestBuilder<NotificationWorker>(15, TimeUnit.MINUTES).build()

    init {
        WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork("noti", ExistingPeriodicWorkPolicy.KEEP, notificationWorkRequest)
    }
}
