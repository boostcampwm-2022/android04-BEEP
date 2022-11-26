package com.lighthouse.presentation.ui.main

sealed class MainEvents {

    object NavigateHome : MainEvents()
    object NavigateList : MainEvents()
    object NavigateSetting : MainEvents()
    object NavigateAddGifticon : MainEvents()
    object NavigateMap : MainEvents()
}
