package com.lighthouse.presentation.ui.detailgifticon

import android.graphics.Rect
import com.lighthouse.domain.model.Gifticon

sealed class GifticonDetailEvent {
    object ScrollDownForUseButtonClicked : GifticonDetailEvent()
    object ShareButtonClicked : GifticonDetailEvent()
    object ShowAllUsedInfoButtonClicked : GifticonDetailEvent()
    data class ShowOriginalImage(val origin: String) : GifticonDetailEvent()
    data class ShowLargeBarcode(val barcode: String) : GifticonDetailEvent()
    object EditButtonClicked : GifticonDetailEvent()
    object ExistEmptyInfo : GifticonDetailEvent()
    data class OnGifticonInfoChanged(val before: Gifticon, val after: Gifticon) : GifticonDetailEvent()
    object ExpireDateClicked : GifticonDetailEvent()
    object UseGifticonButtonClicked : GifticonDetailEvent()
    object UseGifticonComplete : GifticonDetailEvent()
    data class NavigateToCropGifticon(val originPath: String, val croppedRect: Rect) : GifticonDetailEvent()
}
