package com.lighthouse.presentation.ui.gallery.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lighthouse.presentation.R
import com.lighthouse.presentation.binding.loadThumbnailByContentUri
import com.lighthouse.presentation.databinding.ItemGalleryBinding
import com.lighthouse.presentation.model.GalleryUIModel

class GalleryItemViewHolder(
    parent: ViewGroup,
    private val onClick: (GalleryUIModel.Gallery) -> Unit,
    private val selection: GallerySelection,
    private val binding: ItemGalleryBinding = ItemGalleryBinding.bind(
        LayoutInflater.from(parent.context).inflate(R.layout.item_gallery, parent, false)
    )
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: GalleryUIModel.Gallery) {
        binding.dm = GalleryDisplayModel(item) {
            onClick(it)
            binding.selection = selection
        }
        binding.selection = selection
        binding.ivThumbnail.loadThumbnailByContentUri(item.uri)
    }
}
