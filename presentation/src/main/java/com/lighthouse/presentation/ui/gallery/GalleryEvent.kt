package com.lighthouse.presentation.ui.gallery

sealed class GalleryEvent {

    object PopupBackStack : GalleryEvent()

    object CompleteSelect : GalleryEvent()
}
