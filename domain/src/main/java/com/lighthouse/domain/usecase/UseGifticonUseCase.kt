package com.lighthouse.domain.usecase

import com.lighthouse.beep.model.user.UsageHistory
import com.lighthouse.core.exts.currentTime
import com.lighthouse.domain.repository.gifticon.GifticonUsageHistoryRepository
import com.lighthouse.domain.repository.location.LocationRepository
import com.lighthouse.domain.repository.user.UserRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class UseGifticonUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val locationRepository: LocationRepository,
    private val gifticonRepository: GifticonUsageHistoryRepository
) {

    suspend operator fun invoke(gifticonId: String): Result<Unit> {
        val userLocation = locationRepository.getLocations().first()
        val usageHistory = UsageHistory(currentTime, userLocation, 0)

        return gifticonRepository.useGifticon(
            userRepository.getUserId(),
            gifticonId,
            usageHistory
        )
    }
}
