package com.lighthouse.presentation.ui.gallery.adapter.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lighthouse.presentation.R
import com.lighthouse.presentation.databinding.ItemGalleryHeaderBinding
import com.lighthouse.presentation.model.GalleryUIModel

class GalleryHeaderViewHolder(
    parent: ViewGroup,
    private val binding: ItemGalleryHeaderBinding = ItemGalleryHeaderBinding.bind(
        LayoutInflater.from(parent.context).inflate(R.layout.item_gallery_header, parent, false)
    )
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: GalleryUIModel.Header) {
        binding.item = item
    }
}
