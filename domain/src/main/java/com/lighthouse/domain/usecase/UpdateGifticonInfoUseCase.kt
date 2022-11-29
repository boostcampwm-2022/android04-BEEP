package com.lighthouse.domain.usecase

import com.lighthouse.domain.model.Gifticon
import com.lighthouse.domain.repository.GifticonRepository
import javax.inject.Inject

class UpdateGifticonInfoUseCase @Inject constructor(
    private val gifticonRepository: GifticonRepository
) {

    suspend operator fun invoke(gifticon: Gifticon) {
        gifticonRepository.updateGifticon(gifticon)
    }
}
