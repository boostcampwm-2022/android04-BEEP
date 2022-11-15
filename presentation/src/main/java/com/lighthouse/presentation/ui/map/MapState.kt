package com.lighthouse.presentation.ui.map

sealed class MapState {
    object Success : MapState()
    object Loading : MapState()
    object NotFoundSearchResults : MapState()
    object NetworkFailure : MapState()
    object Failure : MapState()
}
