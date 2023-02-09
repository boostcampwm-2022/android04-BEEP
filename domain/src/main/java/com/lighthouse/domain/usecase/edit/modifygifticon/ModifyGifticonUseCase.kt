package com.lighthouse.domain.usecase.edit.modifygifticon

import com.lighthouse.beep.model.exception.auth.AuthenticationException
import com.lighthouse.beep.model.gifticon.GifticonForUpdate
import com.lighthouse.domain.repository.gifticon.GifticonEditRepository
import com.lighthouse.domain.repository.user.UserRepository
import javax.inject.Inject

class ModifyGifticonUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val gifticonRepository: GifticonEditRepository
) {

    suspend operator fun invoke(gifticonForUpdate: GifticonForUpdate): Result<Unit> {
        return if (userRepository.getUserId() == gifticonForUpdate.userId) {
            gifticonRepository.updateGifticon(gifticonForUpdate)
        } else {
            Result.failure(AuthenticationException("UserId 가 기프티콘 소유주와 다릅니다."))
        }
    }
}
