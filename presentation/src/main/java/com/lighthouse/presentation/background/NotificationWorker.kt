package com.lighthouse.presentation.background

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.lighthouse.domain.model.DbResult
import com.lighthouse.domain.model.Gifticon
import com.lighthouse.domain.usecase.GetGifticonsUseCase
import com.lighthouse.core.utils.time.TimeCalculator
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class NotificationWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val notificationHelper: NotificationHelper,
    private val getGifticonsUseCase: GetGifticonsUseCase
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        getNotificationGifticons()
        return Result.success()
    }

    private suspend fun getNotificationGifticons() {
        getGifticonsUseCase.getUsableGifticons().collect { dbResult ->
            if (dbResult is DbResult.Success) {
                val usableGifticons = dbResult.data

                targetDDays.forEach { days ->
                    val targetGifticons =
                        usableGifticons.filter { TimeCalculator.formatDdayToInt(it.expireAt.time) == days }
                    createNotification(targetGifticons, days)
                }
            }
        }
    }

    private fun createNotification(list: List<Gifticon>, remainDays: Int) {
        list.forEach { notificationHelper.applyNotification(it, remainDays) }
    }

    companion object {
        private val targetDDays = listOf(3, 7, 14)
    }
}
