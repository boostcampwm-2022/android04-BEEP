package com.lighthouse.domain.usecase.edit

import com.lighthouse.beep.model.gallery.GalleryImage
import com.lighthouse.beep.model.gifticon.GifticonRecognizeResult
import com.lighthouse.domain.repository.gifticon.GifticonRecognizeRepository
import javax.inject.Inject

class RecognizeGifticonUseCase @Inject constructor(
    private val gifticonRecognizeRepository: GifticonRecognizeRepository
) {

    suspend operator fun invoke(galleryImage: GalleryImage): Result<GifticonRecognizeResult> {
        return gifticonRecognizeRepository.recognize(galleryImage)
    }
}
