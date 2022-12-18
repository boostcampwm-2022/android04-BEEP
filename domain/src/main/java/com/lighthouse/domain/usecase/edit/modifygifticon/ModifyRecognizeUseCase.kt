package com.lighthouse.domain.usecase.edit.modifygifticon

import com.lighthouse.domain.usecase.edit.RecognizeBalanceUseCase
import com.lighthouse.domain.usecase.edit.RecognizeBarcodeUseCase
import com.lighthouse.domain.usecase.edit.RecognizeBrandNameUseCase
import com.lighthouse.domain.usecase.edit.RecognizeExpiredUseCase
import com.lighthouse.domain.usecase.edit.RecognizeGifticonNameUseCase
import java.util.Date
import javax.inject.Inject

class ModifyRecognizeUseCase @Inject constructor(
    private val recognizeGifticonNameUseCase: RecognizeGifticonNameUseCase,
    private val recognizeBrandNameUseCase: RecognizeBrandNameUseCase,
    private val recognizeBarcodeUseCase: RecognizeBarcodeUseCase,
    private val recognizeBalanceUseCase: RecognizeBalanceUseCase,
    private val recognizeExpiredUseCase: RecognizeExpiredUseCase
) {
    suspend fun gifticonName(uri: String): String = recognizeGifticonNameUseCase(uri)

    suspend fun brandName(uri: String): String = recognizeBrandNameUseCase(uri)

    suspend fun barcode(uri: String): String = recognizeBarcodeUseCase(uri)

    suspend fun balance(uri: String): Int = recognizeBalanceUseCase(uri)

    suspend fun expired(uri: String): Date = recognizeExpiredUseCase(uri)
}
