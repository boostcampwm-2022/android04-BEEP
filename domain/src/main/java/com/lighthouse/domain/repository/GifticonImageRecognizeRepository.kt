package com.lighthouse.domain.repository

import com.lighthouse.domain.model.GalleryImage
import com.lighthouse.domain.model.GifticonForAddition
import java.util.Date

interface GifticonImageRecognizeRepository {

    suspend fun recognize(gallery: GalleryImage): GifticonForAddition?

    suspend fun recognizeGifticonName(path: String): String

    suspend fun recognizeBrandName(path: String): String

    suspend fun recognizeBarcode(path: String): String

    suspend fun recognizeBalance(path: String): Int

    suspend fun recognizeExpired(path: String): Date
}
