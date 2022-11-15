package com.lighthouse.presentation.model

import android.net.Uri

sealed class AddGifticonUIModel {
    object Gallery : AddGifticonUIModel()
    data class Gifticon(
        val id: Int,
        val uri: Uri?,
        val invalid: Boolean,
        val isDelete: Boolean
    ) : AddGifticonUIModel()
}
