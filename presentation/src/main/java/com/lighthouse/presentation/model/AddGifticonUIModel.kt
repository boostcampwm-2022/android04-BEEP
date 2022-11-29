package com.lighthouse.presentation.model

import android.net.Uri
import java.util.Date

data class AddGifticonUIModel(
    val id: Long,
    val origin: Uri,
    val hasImage: Boolean,
    val name: String,
    val brandName: String,
    val barcode: EditTextInfo,
    val expiredAt: Date,
    val isCashCard: Boolean,
    val balance: EditTextInfo,
    val memo: String,
    val thumbnailImage: CroppedImage
) {
    val uri: Uri
        get() = thumbnailImage.uri ?: origin
}
