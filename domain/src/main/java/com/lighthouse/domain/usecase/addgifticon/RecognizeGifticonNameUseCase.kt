package com.lighthouse.domain.usecase.addgifticon

import com.lighthouse.domain.repository.GifticonImageRecognizeRepository
import javax.inject.Inject

class RecognizeGifticonNameUseCase @Inject constructor(
    private val gifticonImageRecognizeRepository: GifticonImageRecognizeRepository
) {

    suspend operator fun invoke(uri: String): String {
        return gifticonImageRecognizeRepository.recognizeGifticonName(uri)
    }
}
