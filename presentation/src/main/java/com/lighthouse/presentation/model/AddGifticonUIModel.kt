package com.lighthouse.presentation.model

import android.net.Uri
import java.util.Date

data class AddGifticonUIModel(
    val id: Long,
    val origin: Uri,
    val name: String,
    val brandName: String,
    val barcode: String,
    val expiredAt: Date,
    val isCashCard: Boolean,
    val balance: String,
    val memo: String,
    val brandImage: CroppedImage,
    val thumbnailImage: CroppedImage
) {
    val uri: Uri
        get() = thumbnailImage.uri ?: origin
}
