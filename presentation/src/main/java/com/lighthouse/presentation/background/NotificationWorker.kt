package com.lighthouse.presentation.background

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.lighthouse.domain.model.DbResult
import com.lighthouse.domain.model.Gifticon
import com.lighthouse.domain.usecase.GetGifticonsUseCase
import com.lighthouse.presentation.util.TimeCalculator
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

    private fun createNotification(list: List<Gifticon>, n: Int) {
        list.forEach { notificationHelper.applyNotification(it, n) }
    }

    private suspend fun getNotificationGifticons() {
        getGifticonsUseCase.getUsableGifticons().collect { dbResult ->
            if (dbResult is DbResult.Success) {
                val usableGifticons = dbResult.data
                val remain3Days = usableGifticons.filter { TimeCalculator.formatDdayToInt(it.expireAt.time) == 3 }
                createNotification(remain3Days, 3)
                val remain7Days = usableGifticons.filter { TimeCalculator.formatDdayToInt(it.expireAt.time) == 7 }
                createNotification(remain7Days, 7)
                val remain14Days = usableGifticons.filter { TimeCalculator.formatDdayToInt(it.expireAt.time) == 14 }
                createNotification(remain14Days, 14)
            }
        }
    }
}
