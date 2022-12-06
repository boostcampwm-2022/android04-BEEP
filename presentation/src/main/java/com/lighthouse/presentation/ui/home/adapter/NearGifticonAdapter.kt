package com.lighthouse.presentation.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.lighthouse.presentation.R
import com.lighthouse.presentation.adapter.BindableListAdapter
import com.lighthouse.presentation.databinding.ItemNearGifticonVerticalBinding
import com.lighthouse.presentation.model.GifticonUiModel

class NearGifticonAdapter(
    private val onClick: (GifticonUiModel) -> Unit
) : BindableListAdapter<GifticonUiModel, NearGifticonAdapter.GifticonVerticalItemViewHolder>(diff) {

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

        fun bind(gifticon: GifticonUiModel) {
            val output = binding.ivProduct.context.getFileStreamPath("cropped${gifticon.id}")
            Glide.with(binding.ivProduct)
                .load(output)
                .into(binding.ivProduct)
            binding.gifticon = NearGifticonDisplayModel(gifticon)
            binding.executePendingBindings()
        }
    }

    companion object {
        private val diff = object : DiffUtil.ItemCallback<GifticonUiModel>() {
            override fun areItemsTheSame(oldItem: GifticonUiModel, newItem: GifticonUiModel): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: GifticonUiModel, newItem: GifticonUiModel): Boolean {
                return oldItem == newItem
            }
        }
    }
}
