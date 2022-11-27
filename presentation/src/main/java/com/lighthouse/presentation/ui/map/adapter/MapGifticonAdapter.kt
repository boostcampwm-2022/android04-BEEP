package com.lighthouse.presentation.ui.map.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.lighthouse.domain.model.Gifticon
import com.lighthouse.presentation.adapter.BindableListAdapter

class MapGifticonAdapter : BindableListAdapter<Gifticon, MapGifticonItemViewHolder>(diff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MapGifticonItemViewHolder {
        return MapGifticonItemViewHolder(parent)
    }

    override fun onBindViewHolder(holder: MapGifticonItemViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    companion object {
        private val diff = object : DiffUtil.ItemCallback<Gifticon>() {
            override fun areItemsTheSame(oldItem: Gifticon, newItem: Gifticon): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Gifticon, newItem: Gifticon): Boolean {
                return oldItem == newItem
            }
        }
    }
}
