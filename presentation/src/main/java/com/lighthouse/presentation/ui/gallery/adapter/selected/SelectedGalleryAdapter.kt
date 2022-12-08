package com.lighthouse.presentation.ui.gallery.adapter.selected

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.lighthouse.presentation.adapter.BindableListAdapter
import com.lighthouse.presentation.model.GalleryUIModel

class SelectedGalleryAdapter(
    private val onClickGallery: (GalleryUIModel.Gallery) -> Unit
) : BindableListAdapter<GalleryUIModel.Gallery, SelectedGalleryItemViewHolder>(diff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectedGalleryItemViewHolder {
        return SelectedGalleryItemViewHolder(parent, onClickGallery)
    }

    override fun onBindViewHolder(holder: SelectedGalleryItemViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    companion object {
        private val diff = object : DiffUtil.ItemCallback<GalleryUIModel.Gallery>() {
            override fun areItemsTheSame(oldItem: GalleryUIModel.Gallery, newItem: GalleryUIModel.Gallery): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: GalleryUIModel.Gallery, newItem: GalleryUIModel.Gallery): Boolean {
                return oldItem == newItem
            }
        }
    }
}
