package com.lighthouse.presentation.ui.gallery.adapter.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.lighthouse.presentation.R
import com.lighthouse.presentation.binding.setUIText
import com.lighthouse.presentation.databinding.ItemGalleryBinding
import com.lighthouse.presentation.model.GalleryUIModel
import com.lighthouse.presentation.util.resource.UIText

class GalleryItemViewHolder(
    parent: ViewGroup,
    private val onClick: (GalleryUIModel.Gallery) -> Unit,
    private val binding: ItemGalleryBinding = ItemGalleryBinding.bind(
        LayoutInflater.from(parent.context).inflate(R.layout.item_gallery, parent, false)
    )
) : RecyclerView.ViewHolder(binding.root) {
    private var dm: GalleryDisplayModel? = null

    fun bind(item: GalleryUIModel.Gallery) {
        dm = GalleryDisplayModel(item, onClick)
        binding.dm = dm
    }

    fun bindSelected(item: GalleryUIModel.Gallery) {
        dm?.item = item
        val isSelected = dm?.isSelected ?: false
        val selectedOrder = dm?.selectedOrder ?: UIText.Empty
        binding.apply {
            viewSelected.setUIText(selectedOrder)
            viewSelected.isSelected = isSelected
            viewShadow.isVisible = isSelected
        }
    }
}
