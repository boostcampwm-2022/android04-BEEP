package com.lighthouse.domain.usecase.addgifticon

import com.lighthouse.domain.model.GalleryImage
import com.lighthouse.domain.model.GifticonForAddition
import java.util.Date
import javax.inject.Inject

class RecognizeUseCase @Inject constructor(
    private val recognizeGifticonImageUseCase: RecognizeGifticonImageUseCase,
    private val recognizeGifticonNameUseCase: RecognizeGifticonNameUseCase,
    private val recognizeBrandNameUseCase: RecognizeBrandNameUseCase,
    private val recognizeBarcodeUseCase: RecognizeBarcodeUseCase,
    private val recognizeBalanceUseCase: RecognizeBalanceUseCase,
    private val recognizeExpiredUseCase: RecognizeExpiredUseCase
) {
    suspend fun gifticon(galleryImage: GalleryImage): GifticonForAddition? {
        return recognizeGifticonImageUseCase(galleryImage)
    }

    suspend fun gifticonName(uri: String): String = recognizeGifticonNameUseCase(uri)

    suspend fun brandName(uri: String): String = recognizeBrandNameUseCase(uri)

    suspend fun barcode(uri: String): String = recognizeBarcodeUseCase(uri)

    suspend fun balance(uri: String): Int = recognizeBalanceUseCase(uri)

    suspend fun expired(uri: String): Date = recognizeExpiredUseCase(uri)
}
