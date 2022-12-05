package com.lighthouse.presentation.ui.gallery.adapter

import com.lighthouse.presentation.model.GalleryUIModel
import com.lighthouse.presentation.util.resource.UIText

class GalleryDisplayModel(
    val item: GalleryUIModel.Gallery,
    private val onClick: (GalleryUIModel.Gallery) -> Unit
) {

    val isSelected = item.selectedOrder != -1

    val selectedOrder = if (isSelected) {
        UIText.DynamicString("${item.selectedOrder + 1}")
    } else {
        UIText.Empty
    }

    fun onClickItem() {
        onClick(item)
    }
}
