package com.lighthouse.presentation.ui.gallery.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.lighthouse.presentation.R
import com.lighthouse.presentation.databinding.ItemGalleryBinding
import com.lighthouse.presentation.model.GalleryUIModel

class GalleryItemViewHolder(
    parent: ViewGroup,
    private val binding: ItemGalleryBinding = ItemGalleryBinding.bind(
        LayoutInflater.from(parent.context).inflate(R.layout.item_gallery, parent, false)
    )
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: GalleryUIModel.Gallery) {
        Glide.with(binding.ivThumbnail.context)
            .load(item.uri)
            .into(binding.ivThumbnail)
    }
}
