package com.lighthouse.presentation.background

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.lighthouse.domain.usecase.RemoveExpirationBrandUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class ExpirationBrandWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val removeExpirationBrandUseCase: RemoveExpirationBrandUseCase
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        runCatching { removeExpirationBrandUseCase }
            .onSuccess {
                return Result.success()
            }
            .onFailure {
                return Result.failure()
            }
        return Result.retry()
    }
}
