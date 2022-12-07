package com.lighthouse.presentation.ui.widget

import kotlinx.serialization.Serializable

@Serializable
sealed interface WidgetState {

    @Serializable
    object Loading : WidgetState

    @Serializable
    object Empty : WidgetState

    @Serializable
    object NoExistsLocationPermission : WidgetState

    @Serializable
    data class Available(val gifticons: List<GifticonWidgetData>) : WidgetState

    @Serializable
    data class Unavailable(val message: String) : WidgetState
}

@Serializable
data class GifticonWidgetData(
    val id: String,
    val category: String,
    val name: String,
    val brand: String,
    val distance: Int
)
