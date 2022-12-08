package com.lighthouse.presentation.ui.gallery.adapter.selected

import com.lighthouse.presentation.model.GalleryUIModel

class SelectedGalleryDisplayModel(
    val item: GalleryUIModel.Gallery,
    private val onClick: (GalleryUIModel.Gallery) -> Unit
) {
    fun onClickItem() {
        onClick(item)
    }
}
