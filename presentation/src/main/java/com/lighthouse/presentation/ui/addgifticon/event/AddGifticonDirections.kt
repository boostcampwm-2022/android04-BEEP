package com.lighthouse.presentation.ui.addgifticon.event

import android.net.Uri
import com.lighthouse.presentation.model.GalleryUIModel

sealed class AddGifticonDirections {
    object Back : AddGifticonDirections()
    data class Gallery(val list: List<GalleryUIModel.Gallery>) : AddGifticonDirections()
    data class CropGifticon(val origin: Uri) : AddGifticonDirections()
    data class OriginGifticon(val origin: Uri) : AddGifticonDirections()
}
