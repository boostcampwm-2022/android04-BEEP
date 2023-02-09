package com.lighthouse.domain.usecase

import javax.inject.Inject

class MoveUserIdGifticonUseCase @Inject constructor(
//    private val gifticonRepository: GifticonRepository
) {

    operator fun invoke(oldUserId: String, newUserId: String) {
//        gifticonRepository.moveUserIdGifticon(oldUserId, newUserId)
    }
}
