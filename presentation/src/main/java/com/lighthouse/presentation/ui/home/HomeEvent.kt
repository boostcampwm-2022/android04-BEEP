package com.lighthouse.presentation.ui.home

sealed class HomeEvent {
    object NavigateDataNotExists : HomeEvent()
    object NavigateDataExists : HomeEvent()
}
