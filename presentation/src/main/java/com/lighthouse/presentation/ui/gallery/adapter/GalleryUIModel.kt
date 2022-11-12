package com.lighthouse.presentation.ui.gallery.adapter

import com.lighthouse.domain.model.GalleryImage
import java.util.Date

sealed class GalleryUIModel {
    data class Header(val date: Date) : GalleryUIModel()
    data class Gallery(val image: GalleryImage) : GalleryUIModel()
}
