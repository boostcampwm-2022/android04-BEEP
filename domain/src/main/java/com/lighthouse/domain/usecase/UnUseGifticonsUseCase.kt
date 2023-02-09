package com.lighthouse.domain.usecase

import com.lighthouse.domain.repository.gifticon.GifticonUsageHistoryRepository
import com.lighthouse.domain.repository.user.UserRepository
import javax.inject.Inject

class UnUseGifticonsUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val gifticonRepository: GifticonUsageHistoryRepository
) {

    suspend operator fun invoke(gifticonId: String): Result<Unit> {
        return gifticonRepository.revertUsedGifticon(
            userRepository.getUserId(),
            gifticonId
        )
    }
}
