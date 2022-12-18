package com.lighthouse.domain.usecase

import com.lighthouse.domain.repository.AuthRepository
import com.lighthouse.domain.repository.GifticonRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class HasVariableGifticonUseCase @Inject constructor(
    private val gifticonRepository: GifticonRepository,
    authRepository: AuthRepository
) {

    val userId = authRepository.getCurrentUserId()

    operator fun invoke(): Flow<Boolean> {
        return gifticonRepository.hasUsableGifticon(userId)
    }
}
