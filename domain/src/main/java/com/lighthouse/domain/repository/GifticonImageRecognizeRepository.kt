package com.lighthouse.domain.repository

import com.lighthouse.domain.model.GalleryImage
import com.lighthouse.domain.model.GifticonForAddition

interface GifticonImageRecognizeRepository {

    suspend fun recognize(gallery: GalleryImage): GifticonForAddition?
}
