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

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isNotEmpty()) {
            val flag = payloads.getOrNull(0) as? Int ?: 0
            val item = getItem(position)

            if (holder is AddCandidateGifticonViewHolder && item is AddGifticonItemUIModel.Gifticon) {
                if (flag and UPDATE_BADGE == UPDATE_BADGE) {
                    holder.bindBadge(item)
                }
                if (flag and UPDATE_SELECTED == UPDATE_SELECTED) {
                    holder.bindSelected(item)
                }
            }
        } else {
            onBindViewHolder(holder, position)
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
            }

            override fun getChangePayload(oldItem: AddGifticonItemUIModel, newItem: AddGifticonItemUIModel): Any? {
                if (oldItem is AddGifticonItemUIModel.Gifticon &&
                    newItem is AddGifticonItemUIModel.Gifticon &&
                    newItem.thumbnailImage == oldItem.thumbnailImage
                ) {
                    var flag = 0
                    if (newItem.isDelete != oldItem.isDelete || newItem.isValid != oldItem.isValid) {
                        flag = flag xor UPDATE_BADGE
                    }
                    if (newItem.isSelected != oldItem.isSelected) {
                        flag = flag xor UPDATE_SELECTED
                    }
                    return if (flag != 0) flag else null
                }
                return null
            }
        }

        private const val TYPE_GALLERY = 1
        private const val TYPE_GIFTICON = 2

        private const val UPDATE_BADGE = 1 shl 0
        private const val UPDATE_SELECTED = 1 shl 1
    }
}
