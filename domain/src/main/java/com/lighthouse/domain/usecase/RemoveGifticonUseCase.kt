package com.lighthouse.domain.usecase

import com.lighthouse.domain.repository.GifticonRepository
import javax.inject.Inject

class RemoveGifticonUseCase @Inject constructor(
    private val gifticonRepository: GifticonRepository
) {

    suspend operator fun invoke(gifticonId: String) {
        gifticonRepository.removeGifticon(gifticonId)
    }
}
