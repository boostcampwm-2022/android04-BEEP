package com.lighthouse.domain.usecase

import com.lighthouse.domain.model.GifticonForAddition
import com.lighthouse.domain.repository.AuthRepository
import com.lighthouse.domain.repository.GifticonRepository
import javax.inject.Inject

class SaveGifticonsUseCase @Inject constructor(
    private val gifticonRepository: GifticonRepository,
    private val authRepository: AuthRepository
) {

    suspend operator fun invoke(gifticons: List<GifticonForAddition>) {
        gifticonRepository.saveGifticons(authRepository.getCurrentUserId(), gifticons)
    }
}
