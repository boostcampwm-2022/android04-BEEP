package com.lighthouse.presentation.ui.detailgifticon

sealed class Event {
    object ScrollDownForUseButtonClicked : Event()
    object ShareButtonClicked : Event()
    object ShowAllUsedInfoButtonClicked : Event()
    object EditButtonClicked : Event()
    object ExpireDateClicked : Event()
    object UseGifticonButtonClicked : Event()
}
