package com.lighthouse.presentation.ui.gallery.adapter.selected

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lighthouse.presentation.R
import com.lighthouse.presentation.databinding.ItemSelectedGalleryBinding
import com.lighthouse.presentation.model.GalleryUIModel

class SelectedGalleryItemViewHolder(
    parent: ViewGroup,
    private val onClick: (GalleryUIModel.Gallery) -> Unit,
    private val binding: ItemSelectedGalleryBinding = ItemSelectedGalleryBinding.bind(
        LayoutInflater.from(parent.context).inflate(R.layout.item_selected_gallery, parent, false)
    )
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: GalleryUIModel.Gallery) {
        binding.dm = SelectedGalleryDisplayModel(item, onClick)
    }
}
