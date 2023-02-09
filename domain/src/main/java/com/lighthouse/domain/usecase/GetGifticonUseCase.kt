package com.lighthouse.domain.usecase

import com.lighthouse.beep.model.gifticon.Gifticon
import com.lighthouse.domain.repository.gifticon.GifticonSearchRepository
import com.lighthouse.domain.repository.user.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetGifticonUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val gifticonRepository: GifticonSearchRepository
) {

    operator fun invoke(gifticonId: String): Flow<Gifticon> {
        return gifticonRepository.getGifticon(
            userRepository.getUserId(),
            gifticonId
        )
    }
}
