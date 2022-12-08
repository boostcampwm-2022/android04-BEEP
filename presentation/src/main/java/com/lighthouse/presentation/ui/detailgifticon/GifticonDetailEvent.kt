package com.lighthouse.presentation.ui.detailgifticon

import com.lighthouse.domain.model.Gifticon

sealed class GifticonDetailEvent {
    object ScrollDownForUseButtonClicked : GifticonDetailEvent()
    object ShareButtonClicked : GifticonDetailEvent()
    object ShowAllUsedInfoButtonClicked : GifticonDetailEvent()
    data class ShowOriginalImage(val origin: String) : GifticonDetailEvent()
    object EditButtonClicked : GifticonDetailEvent()
    data class OnGifticonInfoChanged(val before: Gifticon, val after: Gifticon) : GifticonDetailEvent()
    object ExpireDateClicked : GifticonDetailEvent()
    object UseGifticonButtonClicked : GifticonDetailEvent()
    object UseGifticonComplete : GifticonDetailEvent()
}
