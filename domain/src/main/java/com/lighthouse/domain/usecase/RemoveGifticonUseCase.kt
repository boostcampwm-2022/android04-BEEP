package com.lighthouse.domain.usecase

import com.lighthouse.domain.repository.gifticon.GifticonEditRepository
import com.lighthouse.domain.repository.user.UserRepository
import javax.inject.Inject

class RemoveGifticonUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val gifticonRepository: GifticonEditRepository
) {

    suspend operator fun invoke(gifticonId: String): Result<Unit> {
        return gifticonRepository.deleteGifticon(userRepository.getUserId(), gifticonId)
    }
}
