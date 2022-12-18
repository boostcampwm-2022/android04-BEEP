package com.lighthouse.presentation.ui.main

sealed class MainEvent {

    object NavigateAddGifticon : MainEvent()
    data class NavigateMap(val brand: String) : MainEvent()
}
