package com.lighthouse.presentation.ui.gallery.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.lighthouse.presentation.adapter.BindableListAdapter
import com.lighthouse.presentation.model.GalleryUIModel

class GalleryAdapter() : BindableListAdapter<GalleryUIModel, RecyclerView.ViewHolder>(Diff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_HEADER -> GalleryHeaderViewHolder(parent)
            TYPE_GALLERY -> GalleryItemViewHolder(parent)
            else -> throw IllegalArgumentException("잘못된 viewType 입니다.")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = currentList[position]
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
        return when (currentList[position]) {
            is GalleryUIModel.Header -> TYPE_HEADER
            is GalleryUIModel.Gallery -> TYPE_GALLERY
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

        private const val TYPE_HEADER = 1
        private const val TYPE_GALLERY = 2
    }
}
