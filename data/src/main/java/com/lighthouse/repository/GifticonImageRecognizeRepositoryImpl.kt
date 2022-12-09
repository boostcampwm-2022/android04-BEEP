package com.lighthouse.repository

import com.lighthouse.datasource.gifticon.GifticonImageRecognizeSource
import com.lighthouse.domain.model.GalleryImage
import com.lighthouse.domain.model.GifticonForAddition
import com.lighthouse.domain.repository.GifticonImageRecognizeRepository
import java.util.Date
import javax.inject.Inject

class GifticonImageRecognizeRepositoryImpl @Inject constructor(
    private val gifticonImageRecognizeSource: GifticonImageRecognizeSource
) : GifticonImageRecognizeRepository {

    override suspend fun recognize(gallery: GalleryImage): GifticonForAddition? {
        return gifticonImageRecognizeSource.recognize(gallery.id, gallery.contentUri)
    }

    override suspend fun recognizeGifticonName(path: String): String {
        return gifticonImageRecognizeSource.recognizeGifticonName(path)
    }

    override suspend fun recognizeBrandName(path: String): String {
        return gifticonImageRecognizeSource.recognizeBrandName(path)
    }

    override suspend fun recognizeBarcode(path: String): String {
        return gifticonImageRecognizeSource.recognizeBarcode(path)
    }

    override suspend fun recognizeBalance(path: String): Int {
        return gifticonImageRecognizeSource.recognizeBalance(path)
    }

    override suspend fun recognizeExpired(path: String): Date {
        return gifticonImageRecognizeSource.recognizeExpired(path)
    }
}
