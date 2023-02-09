package com.lighthouse.domain.usecase.edit.addgifticon

import com.lighthouse.beep.model.gallery.GalleryImage
import com.lighthouse.beep.model.gifticon.GifticonRecognizeResult
import com.lighthouse.domain.usecase.edit.RecognizeGifticonUseCase
import com.lighthouse.domain.usecase.edit.recognize.RecognizeBalanceUseCase
import com.lighthouse.domain.usecase.edit.recognize.RecognizeBarcodeUseCase
import com.lighthouse.domain.usecase.edit.recognize.RecognizeBrandNameUseCase
import com.lighthouse.domain.usecase.edit.recognize.RecognizeExpiredUseCase
import com.lighthouse.domain.usecase.edit.recognize.RecognizeGifticonNameUseCase
import java.util.Date
import javax.inject.Inject

class AddRecognizeUseCase @Inject constructor(
    private val recognizeGifticonUseCase: RecognizeGifticonUseCase,
    private val recognizeGifticonNameUseCase: RecognizeGifticonNameUseCase,
    private val recognizeBrandNameUseCase: RecognizeBrandNameUseCase,
    private val recognizeBarcodeUseCase: RecognizeBarcodeUseCase,
    private val recognizeBalanceUseCase: RecognizeBalanceUseCase,
    private val recognizeExpiredUseCase: RecognizeExpiredUseCase
) {
    suspend fun gifticon(galleryImage: GalleryImage): Result<GifticonRecognizeResult> {
        return recognizeGifticonUseCase(galleryImage)
    }

    suspend fun gifticonName(uri: String): Result<String> = recognizeGifticonNameUseCase(uri)

    suspend fun brandName(uri: String): Result<String> = recognizeBrandNameUseCase(uri)

    suspend fun barcode(uri: String): Result<String> = recognizeBarcodeUseCase(uri)

    suspend fun balance(uri: String): Result<Int> = recognizeBalanceUseCase(uri)

    suspend fun expired(uri: String): Result<Date> = recognizeExpiredUseCase(uri)
}
