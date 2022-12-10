package com.lighthouse.presentation.ui.gallery.event

sealed class GalleryEvent {

    object PopupBackStack : GalleryEvent()

    object CompleteSelect : GalleryEvent()
}
