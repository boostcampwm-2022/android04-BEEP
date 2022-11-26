package com.lighthouse.presentation.model

import android.graphics.RectF
import android.net.Uri
import java.util.Date

sealed class AddGifticonUIModel {
    object Gallery : AddGifticonUIModel()
    data class Gifticon(
        val id: Long,
        val origin: Uri,
        val name: String,
        val brandName: String,
        val expiredAt: Date,
        val isCashCard: Boolean,
        val balance: String,
        val memo: String,
        val brandUri: Uri?,
        val brandRect: RectF,
        val cropped: Uri?,
        val cropRect: RectF,
        val isDelete: Boolean,
        val isValid: Boolean
    ) : AddGifticonUIModel() {

        val uri: Uri
            get() = cropped ?: origin
    }
}
