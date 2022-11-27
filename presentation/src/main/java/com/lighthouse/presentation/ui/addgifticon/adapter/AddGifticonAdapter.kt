package com.lighthouse.presentation.ui.addgifticon.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.lighthouse.presentation.adapter.BindableListAdapter

class AddGifticonAdapter(
    private val onClickGallery: () -> Unit,
    private val onClickGifticon: (AddGifticonItemUIModel.Gifticon) -> Unit,
    private val onDeleteGifticon: (AddGifticonItemUIModel.Gifticon) -> Unit
) : BindableListAdapter<AddGifticonItemUIModel, RecyclerView.ViewHolder>(diff) {

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
            holder is AddCandidateGifticonViewHolder && item is AddGifticonItemUIModel.Gifticon -> {
                holder.bind(item)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (currentList[position]) {
            AddGifticonItemUIModel.Gallery -> TYPE_GALLERY
            is AddGifticonItemUIModel.Gifticon -> TYPE_GIFTICON
        }
    }

    companion object {
        private val diff = object : DiffUtil.ItemCallback<AddGifticonItemUIModel>() {
            override fun areItemsTheSame(oldItem: AddGifticonItemUIModel, newItem: AddGifticonItemUIModel): Boolean {
                return when {
                    oldItem is AddGifticonItemUIModel.Gallery && newItem is AddGifticonItemUIModel.Gallery -> true
                    oldItem is AddGifticonItemUIModel.Gifticon && newItem is AddGifticonItemUIModel.Gifticon -> {
                        oldItem.id == newItem.id
                    }
                    else -> false
                }
            }

            override fun areContentsTheSame(oldItem: AddGifticonItemUIModel, newItem: AddGifticonItemUIModel): Boolean {
                return oldItem == newItem
//                return when {
//                    oldItem is AddGifticonUIModel.Gallery && newItem is AddGifticonUIModel.Gallery -> true
//                    oldItem is AddGifticonUIModel.Gifticon && newItem is AddGifticonUIModel.Gifticon -> {
//                    }
//                    else -> false
//                }
            }
        }

        private const val TYPE_GALLERY = 1
        private const val TYPE_GIFTICON = 2
    }
}
