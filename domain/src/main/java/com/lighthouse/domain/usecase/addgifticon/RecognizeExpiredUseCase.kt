package com.lighthouse.domain.usecase.addgifticon

import com.lighthouse.domain.repository.GifticonImageRecognizeRepository
import java.util.Date
import javax.inject.Inject

class RecognizeExpiredUseCase @Inject constructor(
    private val gifticonImageRecognizeRepository: GifticonImageRecognizeRepository
) {

    suspend operator fun invoke(uri: String): Date {
        return gifticonImageRecognizeRepository.recognizeExpired(uri)
    }
}
