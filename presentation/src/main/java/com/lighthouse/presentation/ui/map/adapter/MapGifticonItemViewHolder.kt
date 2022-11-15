package com.lighthouse.presentation.ui.map.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lighthouse.domain.model.Gifticon
import com.lighthouse.presentation.R
import com.lighthouse.presentation.databinding.ItemGifticonHorizontalBinding

class MapGifticonItemViewHolder(
    parent: ViewGroup,
    private val binding: ItemGifticonHorizontalBinding = ItemGifticonHorizontalBinding.bind(
        LayoutInflater.from(parent.context).inflate(R.layout.item_gifticon_horizontal, parent, false)
    )
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(gifticon: Gifticon) {
        binding.gifticon = gifticon
    }
}
