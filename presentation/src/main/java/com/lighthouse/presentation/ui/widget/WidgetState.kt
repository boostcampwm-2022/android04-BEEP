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
    data class Available(val gifticons: List<Pair<Map.Entry<String, String>, Int?>>) : WidgetState

    @Serializable
    data class Unavailable(val message: String) : WidgetState
}
