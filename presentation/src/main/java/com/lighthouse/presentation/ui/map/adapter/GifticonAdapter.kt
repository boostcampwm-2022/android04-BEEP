package com.lighthouse.presentation.ui.map.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.lighthouse.domain.model.Gifticon
import com.lighthouse.presentation.adapter.BindableListAdapter
import com.lighthouse.presentation.ui.common.GifticonViewHolderType

class GifticonAdapter(
    private val gifticonViewHolderType: GifticonViewHolderType
) : BindableListAdapter<Gifticon, RecyclerView.ViewHolder>(diff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (gifticonViewHolderType) {
            GifticonViewHolderType.VERTICAL -> GifticonVerticalItemViewHolder(parent)
            GifticonViewHolderType.HORIZONTAL -> GifticonHorizontalItemViewHolder(parent)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is GifticonVerticalItemViewHolder -> holder.bind(currentList[position])
            is GifticonHorizontalItemViewHolder -> holder.bind(currentList[position])
        }
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
