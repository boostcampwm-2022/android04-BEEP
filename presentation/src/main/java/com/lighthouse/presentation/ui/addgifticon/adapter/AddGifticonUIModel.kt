package com.lighthouse.presentation.ui.addgifticon.adapter

import android.net.Uri

sealed class AddGifticonUIModel {
    object Gallery : AddGifticonUIModel()
    data class Gifticon(
        val index: Int,
        val uri: Uri?,
        val invalid: Boolean,
        val isDelete: Boolean
    ) : AddGifticonUIModel()
}
