package com.lighthouse.domain.usecase.addgifticon

import com.lighthouse.domain.repository.GifticonImageRecognizeRepository
import javax.inject.Inject

class RecognizeBalanceUseCase @Inject constructor(
    private val gifticonImageRecognizeRepository: GifticonImageRecognizeRepository
) {

    suspend operator fun invoke(uri: String): Int {
        return gifticonImageRecognizeRepository.recognizeBalance(uri)
    }
}
