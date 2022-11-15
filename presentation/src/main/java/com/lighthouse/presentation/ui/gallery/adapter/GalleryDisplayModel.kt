package com.lighthouse.presentation.ui.gallery.adapter

import com.lighthouse.presentation.model.GalleryUIModel

class GalleryDisplayModel(
    val item: GalleryUIModel.Gallery,
    private val onClick: (GalleryUIModel.Gallery) -> Unit
) {

    fun onClickItem() {
        onClick(item)
    }
}
