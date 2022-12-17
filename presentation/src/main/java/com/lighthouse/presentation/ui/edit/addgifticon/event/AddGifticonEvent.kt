package com.lighthouse.presentation.ui.edit.addgifticon.event

import android.graphics.RectF
import android.net.Uri
import com.lighthouse.presentation.model.GalleryUIModel
import com.lighthouse.presentation.ui.edit.addgifticon.adapter.AddGifticonItemUIModel
import com.lighthouse.presentation.ui.edit.event.EditGifticonCrop
import com.lighthouse.presentation.ui.edit.event.EditGifticonTag
import com.lighthouse.presentation.util.resource.UIText
import java.util.Date

sealed class AddGifticonEvent {

    object PopupBackStack : AddGifticonEvent()
    object ShowCancelConfirmation : AddGifticonEvent()
    object RegistrationCompleted : AddGifticonEvent()
    data class ShowDeleteConfirmation(val gifticon: AddGifticonItemUIModel.Gifticon) : AddGifticonEvent()
    data class NavigateToGallery(val list: List<GalleryUIModel.Gallery> = emptyList()) : AddGifticonEvent()
    data class NavigateToCrop(val crop: EditGifticonCrop, val origin: Uri, val croppedRect: RectF = RectF()) :
        AddGifticonEvent()

    data class ShowOriginGifticon(val origin: Uri) : AddGifticonEvent()
    data class ShowExpiredAtDatePicker(val date: Date) : AddGifticonEvent()
    data class RequestLoading(val loading: Boolean) : AddGifticonEvent()
    data class RequestFocus(val tag: EditGifticonTag) : AddGifticonEvent()
    data class RequestScroll(val tag: EditGifticonTag) : AddGifticonEvent()
    data class ShowSnackBar(val uiText: UIText) : AddGifticonEvent()
}
