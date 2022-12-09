package com.lighthouse.presentation.ui.map

sealed class MapEvent {

    object NavigateHome : MapEvent()
    data class DeleteMarker(val marker: List<com.naver.maps.map.overlay.Marker>) : MapEvent()
}
