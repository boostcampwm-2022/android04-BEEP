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
    val brandName: String,
    val brandNameRectF: RectF,
    val approveBrandName: String,
    val barcode: String,
    val barcodeRectF: RectF,
    val expiredAt: Date,
    val expiredAtRectF: RectF,
    val approveExpiredAt: Boolean,
    val isCashCard: Boolean,
    val balance: String,
    val balanceRectF: RectF,
    val memo: String,
    val gifticonImage: CroppedImage,
    val approveGifticonImage: Boolean,
    val createdDate: String
) {
    val uri: Uri
        get() = gifticonImage.uri ?: origin
}
