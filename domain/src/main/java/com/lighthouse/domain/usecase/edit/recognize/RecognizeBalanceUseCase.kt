package com.lighthouse.domain.usecase.edit.recognize

import com.lighthouse.domain.repository.gifticon.GifticonRecognizeRepository
import javax.inject.Inject

class RecognizeBalanceUseCase @Inject constructor(
    private val gifticonRecognizeRepository: GifticonRecognizeRepository
) {

    suspend operator fun invoke(uri: String): Result<Int> {
        return gifticonRecognizeRepository.recognizeBalance(uri)
    }
}
