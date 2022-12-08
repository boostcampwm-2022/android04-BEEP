package com.lighthouse.presentation.ui.addgifticon.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.lighthouse.presentation.R
import com.lighthouse.presentation.databinding.ItemAddCandidateGifticonBinding

class AddCandidateGifticonViewHolder(
    parent: ViewGroup,
    private val onClick: (AddGifticonItemUIModel.Gifticon) -> Unit,
    private val onDelete: (AddGifticonItemUIModel.Gifticon) -> Unit,
    private val binding: ItemAddCandidateGifticonBinding = ItemAddCandidateGifticonBinding.bind(
        LayoutInflater.from(parent.context).inflate(R.layout.item_add_candidate_gifticon, parent, false)
    )
) : RecyclerView.ViewHolder(binding.root) {

    private var dm: AddCandidateGifticonDisplayModel? = null

    fun bind(item: AddGifticonItemUIModel.Gifticon) {
        dm = AddCandidateGifticonDisplayModel(item, onClick, onDelete)
        binding.dm = dm
    }

    fun bindBadge(item: AddGifticonItemUIModel.Gifticon) {
        dm?.item = item
        val badgeVisible = dm?.badgeVisible ?: false
        val badgeResId = dm?.badgeResId
        binding.ivBadge.apply {
            isVisible = badgeVisible
            if (badgeResId != null) {
                setImageResource(badgeResId)
            }
        }
    }
}
