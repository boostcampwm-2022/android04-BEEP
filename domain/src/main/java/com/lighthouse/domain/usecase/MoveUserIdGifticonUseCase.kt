package com.lighthouse.domain.usecase

import com.lighthouse.domain.repository.GifticonRepository
import javax.inject.Inject

class MoveUserIdGifticonUseCase @Inject constructor(
    private val gifticonRepository: GifticonRepository
) {
    suspend operator fun invoke(oldUserId: String, newUserId: String) {
        gifticonRepository.moveUserIdGifticon(oldUserId, newUserId)
    }
}
