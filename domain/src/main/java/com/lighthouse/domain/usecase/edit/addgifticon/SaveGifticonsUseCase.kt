package com.lighthouse.domain.usecase.edit.addgifticon

import com.lighthouse.beep.model.gifticon.GifticonForAddition
import com.lighthouse.domain.repository.gifticon.GifticonEditRepository
import com.lighthouse.domain.repository.user.UserRepository
import javax.inject.Inject

class SaveGifticonsUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val gifticonRepository: GifticonEditRepository
) {

    suspend operator fun invoke(gifticons: List<GifticonForAddition>): Result<Unit> {
        return gifticonRepository.insertGifticons(
            userRepository.getUserId(),
            gifticons
        )
    }
}
