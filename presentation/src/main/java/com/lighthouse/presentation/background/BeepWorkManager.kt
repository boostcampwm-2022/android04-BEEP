package com.lighthouse.presentation.background

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.lighthouse.presentation.ui.widget.BeepWidgetWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit

class BeepWorkManager(
    @ApplicationContext context: Context
) {

    private val workPolicyKeep = ExistingPeriodicWorkPolicy.KEEP
    private val workPolicyReplace = ExistingPeriodicWorkPolicy.REPLACE

    private val notificationWorkRequest: PeriodicWorkRequest =
        PeriodicWorkRequestBuilder<NotificationWorker>(NOTIFICATION_INTERVAL, TimeUnit.MINUTES).build()

    private val widgetWorkRequest: PeriodicWorkRequest =
        PeriodicWorkRequestBuilder<BeepWidgetWorker>(WIDGET_INTERVAL, TimeUnit.MINUTES).build()

    private val manager = WorkManager.getInstance(context).also {
        it.enqueueUniquePeriodicWork(
            NOTIFICATION_WORK_NAME,
            workPolicyKeep,
            notificationWorkRequest
        )
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
        private const val NOTIFICATION_INTERVAL = 15L // test를 위해 최소 간격인 15분으로 설정. 원래는 24시간.
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
