package com.lighthouse.repository

import com.lighthouse.datasource.gifticon.GifticonImageRecognizeSource
import com.lighthouse.domain.model.GalleryImage
import com.lighthouse.domain.model.GifticonForAddition
import com.lighthouse.domain.repository.GifticonImageRecognizeRepository
import javax.inject.Inject

class GifticonImageRecognizeRepositoryImpl @Inject constructor(
    private val gifticonImageRecognizeSource: GifticonImageRecognizeSource
) : GifticonImageRecognizeRepository {

    override suspend fun recognize(gallery: GalleryImage): GifticonForAddition? {
        return gifticonImageRecognizeSource.recognize(gallery.id, gallery.contentUri)
    }
}
