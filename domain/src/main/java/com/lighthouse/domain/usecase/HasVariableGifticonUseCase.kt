package com.lighthouse.domain.usecase

import com.lighthouse.domain.repository.gifticon.GifticonSearchRepository
import com.lighthouse.domain.repository.user.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class HasVariableGifticonUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val gifticonRepository: GifticonSearchRepository
) {

    operator fun invoke(): Flow<Result<Boolean>> {
        return gifticonRepository.hasGifticon(
            userRepository.getUserId(),
            isUsed = false,
            filterExpired = false
        )
    }
}
