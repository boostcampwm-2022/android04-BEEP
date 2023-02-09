package com.lighthouse.domain.repository.gifticon

import com.lighthouse.beep.model.gallery.GalleryImage
import com.lighthouse.beep.model.gifticon.GifticonRecognizeResult
import java.util.Date

interface GifticonRecognizeRepository {

    suspend fun recognize(gallery: GalleryImage): Result<GifticonRecognizeResult>

    suspend fun recognizeText(path: String): Result<String>

    suspend fun recognizeBarcode(path: String): Result<String>

    suspend fun recognizeBalance(path: String): Result<Int>

    suspend fun recognizeExpired(path: String): Result<Date>
}
