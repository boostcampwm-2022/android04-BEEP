package com.lighthouse.presentation.model

import android.graphics.RectF
import android.net.Uri
import java.util.Date

data class ModifyGifticonUIModel(
    val id: String,
    val userId: String,
    val hasImage: Boolean,
    val oldCroppedUri: Uri,
    val croppedUri: Uri,
    val croppedRect: RectF,
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
    val memo: String
) {
    val originFileName = "origin$id"
}
