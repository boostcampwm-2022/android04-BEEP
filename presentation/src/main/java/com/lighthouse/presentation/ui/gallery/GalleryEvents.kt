package com.lighthouse.presentation.ui.gallery

sealed class GalleryEvents {

    object PopupBackStack : GalleryEvents()

    object CompleteSelect : GalleryEvents()
}
