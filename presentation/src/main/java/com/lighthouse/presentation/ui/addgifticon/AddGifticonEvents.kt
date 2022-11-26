package com.lighthouse.presentation.ui.addgifticon

import android.graphics.RectF
import android.net.Uri

sealed class AddGifticonEvents {

    object PopupBackStack : AddGifticonEvents()
    data class NavigateToGallery(val list: List<Long>) : AddGifticonEvents()
    data class NavigateToCropGifticon(val origin: Uri, val croppedRect: RectF) : AddGifticonEvents()
    data class ShowOriginGifticon(val origin: Uri) : AddGifticonEvents()
}
