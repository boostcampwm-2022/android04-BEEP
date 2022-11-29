package com.lighthouse.presentation.ui.addgifticon

import android.graphics.RectF
import android.net.Uri
import com.lighthouse.presentation.util.resource.UIText
import java.util.Date

sealed class AddGifticonEvent {

    object PopupBackStack : AddGifticonEvent()
    object ShowConfirmation : AddGifticonEvent()
    data class NavigateToGallery(val list: List<Long> = emptyList()) : AddGifticonEvent()
    data class NavigateToCropGifticon(val origin: Uri, val croppedRect: RectF) : AddGifticonEvent()
    data class ShowOriginGifticon(val origin: Uri) : AddGifticonEvent()
    data class ShowExpiredAtDatePicker(val date: Date) : AddGifticonEvent()
    data class RequestFocus(val focus: AddGifticonFocus) : AddGifticonEvent()
    data class ShowSnackBar(val uiText: UIText) : AddGifticonEvent()
}
