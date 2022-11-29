package com.lighthouse.presentation.ui.addgifticon.adapter

import android.net.Uri
import com.lighthouse.presentation.model.CroppedImage

sealed class AddGifticonItemUIModel {
    object Gallery : AddGifticonItemUIModel()
    data class Gifticon(
        val id: Long,
        val origin: Uri,
        val thumbnailImage: CroppedImage,
        val isDelete: Boolean,
        val isValid: Boolean
    ) : AddGifticonItemUIModel() {

        val uri: Uri
            get() = thumbnailImage.uri ?: origin
    }
}
