package com.lighthouse.presentation.ui.detailgifticon

sealed class Event {
    object ScrollDownForUseButtonClicked : Event()
    object ShareButtonClicked : Event()
    object EditButtonClicked : Event()
    object ShowAllUsedInfoButtonClicked : Event()
}
