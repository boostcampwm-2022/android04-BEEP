package com.lighthouse.presentation.ui.gallery.adapter

import com.lighthouse.presentation.model.GalleryUIModel

class GallerySelection(
    private var selected: List<GalleryUIModel.Gallery> = listOf()
) {
    val size
        get() = selected.size

    fun toArrayList(): ArrayList<GalleryUIModel.Gallery> = ArrayList(selected)

    fun isSelected(model: GalleryUIModel.Gallery): Boolean {
        return model in selected
    }

    fun toggle(model: GalleryUIModel.Gallery) {
        selected = if (model in selected) {
            selected.filter {
                it != model
            }
        } else {
            selected + listOf(model)
        }
    }
}
