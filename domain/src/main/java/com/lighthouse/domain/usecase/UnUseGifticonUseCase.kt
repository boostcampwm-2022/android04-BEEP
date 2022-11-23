package com.lighthouse.domain.usecase

import com.lighthouse.domain.repository.GifticonRepository
import javax.inject.Inject

class UnUseGifticonUseCase @Inject constructor(
    private val gifticonRepository: GifticonRepository
) {
    suspend operator fun invoke(gifticonId: String) {
        gifticonRepository.unUseGifticon(gifticonId)
    }
}
