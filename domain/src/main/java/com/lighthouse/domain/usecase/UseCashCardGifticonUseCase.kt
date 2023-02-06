package com.lighthouse.domain.usecase

import com.lighthouse.core.exts.currentTime
import com.lighthouse.domain.model.UsageHistory
import com.lighthouse.domain.repository.GifticonRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class UseCashCardGifticonUseCase @Inject constructor(
    private val gifticonRepository: GifticonRepository,
    private val getUserLocationUseCase: GetUserLocationUseCase
) {

    suspend operator fun invoke(gifticonId: String, amount: Int, hasLocationPermission: Boolean) {
        val userLocation = if (hasLocationPermission) getUserLocationUseCase().first() else null
        val usageHistory = UsageHistory(currentTime, userLocation, amount)

        gifticonRepository.useCashCardGifticon(gifticonId, amount, usageHistory)
    }
}
