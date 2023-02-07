package com.lighthouse.presentation.ui.map.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.lighthouse.presentation.R
import com.lighthouse.presentation.adapter.BindableListAdapter
import com.lighthouse.presentation.databinding.ItemGifticonHorizontalBinding
import com.lighthouse.presentation.databinding.ItemGifticonVerticalBinding
import com.lighthouse.presentation.model.GifticonUIModel
import com.lighthouse.presentation.ui.common.GifticonViewHolderType

class GifticonAdapter(
    private val gifticonViewHolderType: GifticonViewHolderType,
    private val onClick: (GifticonUIModel) -> Unit
) : BindableListAdapter<GifticonUIModel, RecyclerView.ViewHolder>(diff) {

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
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_gifticon_horizontal, parent, false)
        )
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                onClick(currentList[absoluteAdapterPosition])
            }
        }

        fun bind(gifticon: GifticonUIModel) {
            binding.gifticon = gifticon
        }
    }

    inner class GifticonVerticalItemViewHolder(
        parent: ViewGroup,
        private val binding: ItemGifticonVerticalBinding = ItemGifticonVerticalBinding.bind(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_gifticon_vertical, parent, false)
        )
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                onClick(currentList[absoluteAdapterPosition])
            }
        }

        fun bind(gifticon: GifticonUIModel) {
            binding.gifticon = gifticon
        }
    }

    companion object {
        private val diff = object : DiffUtil.ItemCallback<GifticonUIModel>() {
            override fun areItemsTheSame(
                oldItem: GifticonUIModel,
                newItem: GifticonUIModel
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: GifticonUIModel,
                newItem: GifticonUIModel
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}
