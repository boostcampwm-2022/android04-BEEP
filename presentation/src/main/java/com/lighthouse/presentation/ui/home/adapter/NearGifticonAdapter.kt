package com.lighthouse.presentation.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.lighthouse.presentation.R
import com.lighthouse.presentation.adapter.BindableListAdapter
import com.lighthouse.presentation.databinding.ItemNearGifticonVerticalBinding
import com.lighthouse.presentation.model.GifticonWithDistanceUIModel

class NearGifticonAdapter(
    private val onClick: (GifticonWithDistanceUIModel) -> Unit
) : BindableListAdapter<GifticonWithDistanceUIModel, NearGifticonAdapter.GifticonVerticalItemViewHolder>(diff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GifticonVerticalItemViewHolder {
        return GifticonVerticalItemViewHolder(parent)
    }

    override fun onBindViewHolder(holder: GifticonVerticalItemViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    inner class GifticonVerticalItemViewHolder(
        parent: ViewGroup,
        private val binding: ItemNearGifticonVerticalBinding = ItemNearGifticonVerticalBinding.bind(
            LayoutInflater.from(parent.context).inflate(R.layout.item_near_gifticon_vertical, parent, false)
        )
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                onClick(currentList[absoluteAdapterPosition])
            }
        }

        fun bind(gifticon: GifticonWithDistanceUIModel) {
            binding.gifticon = NearGifticonDisplayModel(gifticon)
        }
    }

    companion object {
        private val diff = object : DiffUtil.ItemCallback<GifticonWithDistanceUIModel>() {
            override fun areItemsTheSame(oldItem: GifticonWithDistanceUIModel, newItem: GifticonWithDistanceUIModel): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: GifticonWithDistanceUIModel, newItem: GifticonWithDistanceUIModel): Boolean {
                return oldItem == newItem
            }
        }
    }
}
