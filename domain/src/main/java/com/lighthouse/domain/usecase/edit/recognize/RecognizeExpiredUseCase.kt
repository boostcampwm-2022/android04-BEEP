package com.lighthouse.domain.usecase.edit.recognize

import com.lighthouse.domain.repository.gifticon.GifticonRecognizeRepository
import java.util.Date
import javax.inject.Inject

class RecognizeExpiredUseCase @Inject constructor(
    private val gifticonRecognizeRepository: GifticonRecognizeRepository
) {

    suspend operator fun invoke(uri: String): Result<Date> {
        return gifticonRecognizeRepository.recognizeExpired(uri)
    }
}
