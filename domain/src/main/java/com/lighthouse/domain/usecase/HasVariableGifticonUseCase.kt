package com.lighthouse.domain.usecase

import com.lighthouse.domain.repository.GifticonRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class HasVariableGifticonUseCase @Inject constructor(
    private val gifticonRepository: GifticonRepository
) {

    operator fun invoke(): Flow<Boolean> {
        return gifticonRepository.hasVariableGifticon()
    }
}
