package com.lighthouse.presentation.ui.map.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lighthouse.domain.model.Gifticon
import com.lighthouse.presentation.R
import com.lighthouse.presentation.databinding.ItemGifticonVerticalBinding

class GifticonVerticalItemViewHolder(
    parent: ViewGroup,
    private val binding: ItemGifticonVerticalBinding = ItemGifticonVerticalBinding.bind(
        LayoutInflater.from(parent.context).inflate(R.layout.item_gifticon_vertical, parent, false)
    )
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(gifticon: Gifticon) {
        binding.gifticon = gifticon
    }
}
