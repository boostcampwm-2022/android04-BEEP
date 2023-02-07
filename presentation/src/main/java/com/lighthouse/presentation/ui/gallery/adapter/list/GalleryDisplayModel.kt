package com.lighthouse.presentation.ui.gallery.adapter.list

import com.lighthouse.core.android.utils.resource.UIText
import com.lighthouse.presentation.model.GalleryUIModel

class GalleryDisplayModel(
    var item: GalleryUIModel.Gallery,
    private val onClick: (GalleryUIModel.Gallery) -> Unit
) {
    val isSelected
        get() = item.selectedOrder != -1

    val selectedOrder
        get() = if (isSelected) {
            UIText.DynamicString("${item.selectedOrder + 1}")
        } else {
            UIText.Empty
        }

    fun onClickItem() {
        onClick(item)
    }
}
