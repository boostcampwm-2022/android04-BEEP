package com.lighthouse.domain.usecase.edit

import com.lighthouse.domain.repository.GifticonImageRecognizeRepository
import javax.inject.Inject

class RecognizeBrandNameUseCase @Inject constructor(
    private val gifticonImageRecognizeRepository: GifticonImageRecognizeRepository
) {

    suspend operator fun invoke(uri: String): String {
        return gifticonImageRecognizeRepository.recognizeBrandName(uri)
    }
}
