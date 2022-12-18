package com.lighthouse.presentation.ui.edit.modifygifticon.event

import android.graphics.RectF
import com.lighthouse.presentation.model.ModifyGifticonUIModel
import com.lighthouse.presentation.util.resource.UIText
import java.util.Date

sealed class ModifyGifticonEvent {

    object PopupBackStack : ModifyGifticonEvent()
    object ShowCancelConfirmation : ModifyGifticonEvent()
    object ModifyCompleted : ModifyGifticonEvent()
    data class ShowDeleteConfirmation(val gifticon: ModifyGifticonUIModel) : ModifyGifticonEvent()
    data class NavigateToCrop(val crop: ModifyGifticonCrop, val originFileName: String, val croppedRect: RectF) :
        ModifyGifticonEvent()

    data class ShowOriginGifticon(val originFileName: String) : ModifyGifticonEvent()
    data class ShowExpiredAtDatePicker(val date: Date) : ModifyGifticonEvent()
    data class RequestLoading(val loading: Boolean) : ModifyGifticonEvent()
    data class RequestFocus(val tag: ModifyGifticonTag) : ModifyGifticonEvent()
    data class RequestScroll(val tag: ModifyGifticonTag) : ModifyGifticonEvent()
    data class ShowSnackBar(val uiText: UIText) : ModifyGifticonEvent()
}
