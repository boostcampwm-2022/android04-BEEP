package com.lighthouse.presentation.ui.map.event

sealed class MarkerClickEvent {
    object AllGifticon : MarkerClickEvent()
    data class BrandGifticon(val brandName: String) : MarkerClickEvent()
}
