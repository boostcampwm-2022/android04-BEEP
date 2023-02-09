package com.lighthouse.domain.usecase.edit.recognize

import com.lighthouse.domain.repository.gifticon.GifticonRecognizeRepository
import javax.inject.Inject

class RecognizeBarcodeUseCase @Inject constructor(
    private val gifticonRecognizeRepository: GifticonRecognizeRepository
) {

    suspend operator fun invoke(uri: String): Result<String> {
        return gifticonRecognizeRepository.recognizeBarcode(uri)
    }
}
