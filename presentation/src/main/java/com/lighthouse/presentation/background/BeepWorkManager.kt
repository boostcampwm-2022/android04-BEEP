package com.lighthouse.presentation.background

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.lighthouse.presentation.ui.widget.BeepWidgetWorker
import com.lighthouse.presentation.util.TimeCalculator.calculateAfterDateDiffHour
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit

class BeepWorkManager(
    @ApplicationContext context: Context
) {

    private val workPolicyKeep = ExistingPeriodicWorkPolicy.KEEP
    private val workPolicyReplace = ExistingPeriodicWorkPolicy.REPLACE

    private val notificationWorkRequest: PeriodicWorkRequest =
        PeriodicWorkRequestBuilder<NotificationWorker>(
            DAY_INTERVAL,
            TimeUnit.HOURS
        ).setInitialDelay(
            calculateAfterDateDiffHour(),
            TimeUnit.HOURS
        ).build()

    private val widgetWorkRequest: PeriodicWorkRequest =
        PeriodicWorkRequestBuilder<BeepWidgetWorker>(WIDGET_INTERVAL, TimeUnit.MINUTES).build()

    private val brandRemoveWorker: PeriodicWorkRequest =
        PeriodicWorkRequestBuilder<NotificationWorker>(
            DAY_INTERVAL,
            TimeUnit.HOURS
        ).setInitialDelay(
            calculateAfterDateDiffHour(),
            TimeUnit.HOURS
        ).build()

    private val manager = WorkManager.getInstance(context).also {
        it.enqueueUniquePeriodicWork(
            NOTIFICATION_WORK_NAME,
            workPolicyKeep,
            notificationWorkRequest
        )
    }

    init {
        WorkManager.getInstance(context).also {
            it.enqueueUniquePeriodicWork(
                BRAND_REMOVE_WORK_NAME,
                workPolicyKeep,
                brandRemoveWorker
            )
        }
    }

    /**
     * 새로고침이나 시작할때 실행할 함수
     * @param force 만약 새로고침
     */
    fun widgetEnqueue(force: Boolean = false) {
        val policy = when (force) {
            true -> workPolicyReplace
            false -> workPolicyKeep
        }
        manager.enqueueUniquePeriodicWork(
            WIDGET_WORK_NAME,
            policy,
            widgetWorkRequest
        )
    }

    /**
     * WidgetReceiver에서 onDisabled때 실행할 함수
     */
    fun widgetCancel() {
        manager.cancelUniqueWork(WIDGET_WORK_NAME)
    }

    fun notificationCancel() {
        manager.cancelUniqueWork(NOTIFICATION_WORK_NAME)
    }

    fun notificationEnqueue() {
        manager.enqueueUniquePeriodicWork(
            NOTIFICATION_WORK_NAME,
            workPolicyKeep,
            notificationWorkRequest
        )
    }

    companion object {
        private const val NOTIFICATION_WORK_NAME = "notification"
        private const val BRAND_REMOVE_WORK_NAME = "brand"
        private const val DAY_INTERVAL = 24L
        private const val WIDGET_WORK_NAME = "widget"
        private const val WIDGET_INTERVAL = 15L

        @Volatile
        private var instance: BeepWorkManager? = null

        fun getInstance(context: Context): BeepWorkManager =
            instance ?: synchronized(this) {
                BeepWorkManager(context).also {
                    instance = it
                }
            }
    }
}
