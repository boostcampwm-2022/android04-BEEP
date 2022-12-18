package com.lighthouse.domain.usecase

import com.lighthouse.domain.model.UsageHistory
import com.lighthouse.domain.repository.GifticonRepository
import com.lighthouse.domain.util.currentTime
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class UseGifticonUseCase @Inject constructor(
    private val gifticonRepository: GifticonRepository,
    private val getUserLocationUseCase: GetUserLocationUseCase
) {
    suspend operator fun invoke(gifticonId: String, hasLocationPermission: Boolean) {
        val userLocation = if (hasLocationPermission) getUserLocationUseCase().first() else null
        val usageHistory = UsageHistory(currentTime, userLocation, 0)

        gifticonRepository.useGifticon(gifticonId, usageHistory)
    }
}
