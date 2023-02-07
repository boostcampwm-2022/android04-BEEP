package com.lighthouse.repository

import android.net.Uri
import com.lighthouse.beep.model.gallery.GalleryImage
import com.lighthouse.beep.model.gifticon.GifticonForAddition
import com.lighthouse.datasource.gifticon.GifticonImageRecognizeSource
import com.lighthouse.domain.repository.GifticonImageRecognizeRepository
import java.util.Date
import javax.inject.Inject

class GifticonImageRecognizeRepositoryImpl @Inject constructor(
    private val gifticonImageRecognizeSource: GifticonImageRecognizeSource
) : GifticonImageRecognizeRepository {

    override suspend fun recognize(gallery: GalleryImage): GifticonForAddition? {
        return gifticonImageRecognizeSource.recognize(gallery.id, Uri.parse(gallery.contentUri))
    }

    override suspend fun recognizeGifticonName(path: String): String {
        return gifticonImageRecognizeSource.recognizeGifticonName(Uri.parse(path))
    }

    override suspend fun recognizeBrandName(path: String): String {
        return gifticonImageRecognizeSource.recognizeBrandName(Uri.parse(path))
    }

    override suspend fun recognizeBarcode(path: String): String {
        return gifticonImageRecognizeSource.recognizeBarcode(Uri.parse(path))
    }

    override suspend fun recognizeBalance(path: String): Int {
        return gifticonImageRecognizeSource.recognizeBalance(Uri.parse(path))
    }

    override suspend fun recognizeExpired(path: String): Date {
        return gifticonImageRecognizeSource.recognizeExpired(Uri.parse(path))
    }
}
