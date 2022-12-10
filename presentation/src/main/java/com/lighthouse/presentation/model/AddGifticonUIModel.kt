package com.lighthouse.presentation.model

import android.graphics.RectF
import android.net.Uri
import java.util.Date

data class AddGifticonUIModel(
    val id: Long,
    val origin: Uri,
    val hasImage: Boolean,
    val name: String,
    val nameRectF: RectF,
    val approveBrandName: String,
    val brandName: String,
    val brandNameRectF: RectF,
    val barcode: String,
    val barcodeRectF: RectF,
    val expiredAt: Date,
    val expiredAtRectF: RectF,
    val isCashCard: Boolean,
    val balance: String,
    val balanceRectF: RectF,
    val memo: String,
    val approveGifticonImage: Boolean,
    val gifticonImage: CroppedImage
) {
    val uri: Uri
        get() = gifticonImage.uri ?: origin
}
