package com.lighthouse.presentation.ui.map.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.lighthouse.domain.model.Gifticon
import com.lighthouse.presentation.R
import com.lighthouse.presentation.adapter.BindableListAdapter
import com.lighthouse.presentation.binding.loadWithFileStreamPath
import com.lighthouse.presentation.databinding.ItemGifticonHorizontalBinding
import com.lighthouse.presentation.databinding.ItemGifticonVerticalBinding
import com.lighthouse.presentation.ui.common.GifticonViewHolderType

class GifticonAdapter(
    private val gifticonViewHolderType: GifticonViewHolderType,
    private val onClick: (Gifticon) -> Unit
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

    inner class GifticonHorizontalItemViewHolder(
        parent: ViewGroup,
        private val binding: ItemGifticonHorizontalBinding = ItemGifticonHorizontalBinding.bind(
            LayoutInflater.from(parent.context).inflate(R.layout.item_gifticon_horizontal, parent, false)
        )
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                onClick(currentList[absoluteAdapterPosition])
            }
        }

        fun bind(gifticon: Gifticon) {
            binding.ivProduct.loadWithFileStreamPath("cropped${gifticon.id}")
            binding.gifticon = gifticon
            binding.executePendingBindings()
        }
    }

    inner class GifticonVerticalItemViewHolder(
        parent: ViewGroup,
        private val binding: ItemGifticonVerticalBinding = ItemGifticonVerticalBinding.bind(
            LayoutInflater.from(parent.context).inflate(R.layout.item_gifticon_vertical, parent, false)
        )
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                onClick(currentList[absoluteAdapterPosition])
            }
        }

        fun bind(gifticon: Gifticon) {
            binding.ivProduct.loadWithFileStreamPath("cropped${gifticon.id}")
            binding.gifticon = gifticon
            binding.executePendingBindings()
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
