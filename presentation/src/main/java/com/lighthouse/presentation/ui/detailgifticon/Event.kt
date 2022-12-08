package com.lighthouse.presentation.ui.detailgifticon

import com.lighthouse.domain.model.Gifticon

sealed class Event {
    object ScrollDownForUseButtonClicked : Event()
    object ShareButtonClicked : Event()
    object ShowAllUsedInfoButtonClicked : Event()
    data class ShowOriginalImage(val origin: String) : Event()
    object EditButtonClicked : Event()
    data class OnGifticonInfoChanged(val before: Gifticon, val after: Gifticon) : Event()
    object ExpireDateClicked : Event()
    object UseGifticonButtonClicked : Event()
    object UseGifticonComplete : Event()
}
