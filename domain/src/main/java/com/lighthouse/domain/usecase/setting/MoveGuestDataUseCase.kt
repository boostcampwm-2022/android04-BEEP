package com.lighthouse.domain.usecase.setting

import com.lighthouse.domain.repository.gifticon.GifticonEditRepository
import com.lighthouse.domain.repository.user.UserRepository
import javax.inject.Inject

class MoveGuestDataUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val gifticonRepository: GifticonEditRepository
) {

    suspend operator fun invoke(uid: String): Result<Unit> = runCatching {
        userRepository.transferData(uid).getOrThrow()
        gifticonRepository.transferGifticon(userRepository.getUserId(), uid).getOrThrow()
    }
}
