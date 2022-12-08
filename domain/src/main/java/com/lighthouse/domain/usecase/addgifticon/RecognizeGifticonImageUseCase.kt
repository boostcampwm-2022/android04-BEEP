package com.lighthouse.domain.usecase.addgifticon

import com.lighthouse.domain.model.GalleryImage
import com.lighthouse.domain.model.GifticonForAddition
import com.lighthouse.domain.repository.GifticonImageRecognizeRepository
import javax.inject.Inject

class RecognizeGifticonImageUseCase @Inject constructor(
    private val gifticonImageRecognizeRepository: GifticonImageRecognizeRepository
) {

    suspend operator fun invoke(galleryImage: GalleryImage): GifticonForAddition? {
        return gifticonImageRecognizeRepository.recognize(galleryImage)
    }
}
