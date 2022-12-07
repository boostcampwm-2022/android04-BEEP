package com.lighthouse.presentation.ui.map

sealed class Event {

    data class NavigateBrand(val brand: String) : Event()
}
