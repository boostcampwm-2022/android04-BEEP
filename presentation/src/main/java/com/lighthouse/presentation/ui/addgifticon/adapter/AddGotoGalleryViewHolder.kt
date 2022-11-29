package com.lighthouse.presentation.ui.addgifticon.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lighthouse.presentation.R
import com.lighthouse.presentation.databinding.ItemAddGotoGalleryBinding

class AddGotoGalleryViewHolder(
    parent: ViewGroup,
    onClick: () -> Unit,
    binding: ItemAddGotoGalleryBinding = ItemAddGotoGalleryBinding.bind(
        LayoutInflater.from(parent.context).inflate(R.layout.item_add_goto_gallery, parent, false)
    )
) : RecyclerView.ViewHolder(binding.root) {

    init {
        itemView.setOnClickListener {
            onClick()
        }
    }
}
