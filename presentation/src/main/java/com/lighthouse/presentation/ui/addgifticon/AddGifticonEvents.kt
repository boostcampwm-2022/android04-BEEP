package com.lighthouse.presentation.ui.addgifticon

import android.graphics.RectF
import android.net.Uri
import com.lighthouse.presentation.util.resource.UIText
import java.util.Date

sealed class AddGifticonEvents {

    object PopupBackStack : AddGifticonEvents()
    object ShowConfirmation : AddGifticonEvents()
    data class NavigateToGallery(val list: List<Long>) : AddGifticonEvents()
    data class NavigateToCropGifticon(val origin: Uri, val croppedRect: RectF) : AddGifticonEvents()
    data class ShowOriginGifticon(val origin: Uri) : AddGifticonEvents()
    data class ShowExpiredAtDatePicker(val date: Date) : AddGifticonEvents()
    data class RequestFocus(val focus: AddGifticonFocus) : AddGifticonEvents()
    data class ShowSnackBar(val uiText: UIText) : AddGifticonEvents()
}
