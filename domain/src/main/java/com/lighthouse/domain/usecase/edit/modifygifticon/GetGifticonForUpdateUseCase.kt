package com.lighthouse.domain.usecase.edit.modifygifticon

import com.lighthouse.beep.model.gifticon.GifticonForUpdate
import com.lighthouse.domain.repository.AuthRepository
import com.lighthouse.domain.repository.GifticonRepository
import javax.inject.Inject

class GetGifticonForUpdateUseCase @Inject constructor(
    private val gifticonRepository: GifticonRepository,
    private val authRepository: AuthRepository
) {

    suspend operator fun invoke(id: String): GifticonForUpdate? {
        return gifticonRepository.getGifticonCrop(authRepository.getCurrentUserId(), id)
    }
}
