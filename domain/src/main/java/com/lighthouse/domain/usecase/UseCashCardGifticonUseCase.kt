package com.lighthouse.domain.usecase

import com.lighthouse.beep.model.user.UsageHistory
import com.lighthouse.core.exts.currentTime
import com.lighthouse.domain.repository.gifticon.GifticonUsageHistoryRepository
import com.lighthouse.domain.repository.location.LocationRepository
import com.lighthouse.domain.repository.user.UserRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class UseCashCardGifticonUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val gifticonRepository: GifticonUsageHistoryRepository,
    private val locationRepository: LocationRepository
) {

    suspend operator fun invoke(gifticonId: String, amount: Int): Result<Unit> {
        val userLocation = locationRepository.getLocations().first()
        val usageHistory = UsageHistory(currentTime, userLocation, amount)

        return gifticonRepository.useCashCardGifticon(
            userRepository.getUserId(),
            gifticonId,
            amount,
            usageHistory
        )
    }
}
