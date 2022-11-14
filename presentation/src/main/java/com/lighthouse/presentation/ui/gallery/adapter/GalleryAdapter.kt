package com.lighthouse.presentation.ui.gallery.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.lighthouse.presentation.adapter.BindablePagingAdapter
import com.lighthouse.presentation.model.GalleryUIModel

class GalleryAdapter(
    private val onClickGallery: (GalleryUIModel.Gallery) -> Unit
) : BindablePagingAdapter<GalleryUIModel, RecyclerView.ViewHolder>(Diff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_HEADER -> GalleryHeaderViewHolder(parent)
            TYPE_GALLERY -> GalleryItemViewHolder(parent, onClickGallery)
            else -> throw IllegalArgumentException("잘못된 viewType 입니다.")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when {
            holder is GalleryHeaderViewHolder && item is GalleryUIModel.Header -> {
                holder.bind(item)
            }
            holder is GalleryItemViewHolder && item is GalleryUIModel.Gallery -> {
                holder.bind(item)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is GalleryUIModel.Header -> TYPE_HEADER
            is GalleryUIModel.Gallery -> TYPE_GALLERY
            else -> throw IllegalArgumentException("잘못된 viewType 입니다.")
        }
    }

    companion object {
        private val Diff = object : DiffUtil.ItemCallback<GalleryUIModel>() {
            override fun areItemsTheSame(oldItem: GalleryUIModel, newItem: GalleryUIModel): Boolean {
                return when {
                    oldItem is GalleryUIModel.Header && newItem is GalleryUIModel.Header -> {
                        oldItem.date == newItem.date
                    }
                    oldItem is GalleryUIModel.Gallery && newItem is GalleryUIModel.Gallery -> {
                        oldItem.id == newItem.id
                    }
                    else -> false
                }
            }

            override fun areContentsTheSame(oldItem: GalleryUIModel, newItem: GalleryUIModel): Boolean {
                return oldItem == newItem
            }
        }

        const val TYPE_HEADER = 1
        const val TYPE_GALLERY = 2
    }
}
