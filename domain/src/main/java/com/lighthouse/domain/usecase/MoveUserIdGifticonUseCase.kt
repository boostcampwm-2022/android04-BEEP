package com.lighthouse.domain.usecase

import com.lighthouse.domain.repository.GifticonRepository
import javax.inject.Inject

class MoveUserIdGifticonUseCase @Inject constructor(
    private val gifticonRepository: GifticonRepository
) {
    suspend operator fun invoke(userId: String) {
        gifticonRepository.moveUserIdGifticon(userId)
    }
}
