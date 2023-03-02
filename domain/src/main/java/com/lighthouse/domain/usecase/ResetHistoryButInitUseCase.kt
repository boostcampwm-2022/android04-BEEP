package com.lighthouse.domain.usecase

import com.lighthouse.domain.repository.GifticonRepository
import javax.inject.Inject

class ResetHistoryButInitUseCase @Inject constructor(
    private val repository: GifticonRepository,
) {
    suspend operator fun invoke(gifticonId: String) {
        repository.resetHistoryButInit(gifticonId)
    }
}
