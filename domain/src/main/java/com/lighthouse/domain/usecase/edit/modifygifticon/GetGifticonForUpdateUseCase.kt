package com.lighthouse.domain.usecase.edit.modifygifticon

import com.lighthouse.beep.model.gifticon.GifticonWithCrop
import com.lighthouse.domain.repository.gifticon.GifticonSearchRepository
import com.lighthouse.domain.repository.user.UserRepository
import javax.inject.Inject

class GetGifticonForUpdateUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val gifticonRepository: GifticonSearchRepository
) {

    suspend operator fun invoke(gifticonId: String): Result<GifticonWithCrop> {
        return gifticonRepository.getGifticonCrop(
            userRepository.getUserId(),
            gifticonId
        )
    }
}
