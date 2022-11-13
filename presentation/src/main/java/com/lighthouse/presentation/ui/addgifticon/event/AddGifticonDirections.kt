package com.lighthouse.presentation.ui.addgifticon.event

import android.net.Uri

sealed class AddGifticonDirections {
    object Back : AddGifticonDirections()
    data class Gallery(val list: List<Uri>) : AddGifticonDirections()
    data class CropGifticon(val origin: Uri) : AddGifticonDirections()
    data class OriginGifticon(val origin: Uri) : AddGifticonDirections()
}
