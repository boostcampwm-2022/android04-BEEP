package com.lighthouse.presentation.ui.addgifticon.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.lighthouse.presentation.adapter.BindableListAdapter
import com.lighthouse.presentation.model.AddGifticonUIModel

class AddGifticonAdapter(
    private val onClickGallery: () -> Unit,
    private val onClickGifticon: (AddGifticonUIModel.Gifticon) -> Unit,
    private val onDeleteGifticon: (AddGifticonUIModel.Gifticon) -> Unit
) : BindableListAdapter<AddGifticonUIModel, RecyclerView.ViewHolder>(Diff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_GALLERY -> AddGotoGalleryViewHolder(parent, onClickGallery)
            TYPE_GIFTICON -> AddCandidateGifticonViewHolder(parent, onClickGifticon, onDeleteGifticon)
            else -> throw IllegalArgumentException("잘못된 viewType 입니다.")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = currentList[position]
        when {
            holder is AddCandidateGifticonViewHolder && item is AddGifticonUIModel.Gifticon -> {
                holder.bind(item)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (currentList[position]) {
            AddGifticonUIModel.Gallery -> TYPE_GALLERY
            is AddGifticonUIModel.Gifticon -> TYPE_GIFTICON
        }
    }

    companion object {
        private val Diff = object : DiffUtil.ItemCallback<AddGifticonUIModel>() {
            override fun areItemsTheSame(oldItem: AddGifticonUIModel, newItem: AddGifticonUIModel): Boolean {
                return when {
                    oldItem is AddGifticonUIModel.Gallery && newItem is AddGifticonUIModel.Gallery -> true
                    oldItem is AddGifticonUIModel.Gifticon && newItem is AddGifticonUIModel.Gifticon -> {
                        oldItem.id == newItem.id
                    }
                    else -> false
                }
            }

            override fun areContentsTheSame(oldItem: AddGifticonUIModel, newItem: AddGifticonUIModel): Boolean {
                return oldItem == newItem
            }
        }

        private const val TYPE_GALLERY = 1
        private const val TYPE_GIFTICON = 2
    }
}
