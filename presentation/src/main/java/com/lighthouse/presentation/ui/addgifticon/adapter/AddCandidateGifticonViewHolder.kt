package com.lighthouse.presentation.ui.addgifticon.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lighthouse.presentation.R
import com.lighthouse.presentation.binding.loadUriWithoutCache
import com.lighthouse.presentation.databinding.ItemAddCandidateGifticonBinding

class AddCandidateGifticonViewHolder(
    parent: ViewGroup,
    private val onClick: (AddGifticonItemUIModel.Gifticon) -> Unit,
    private val onDelete: (AddGifticonItemUIModel.Gifticon) -> Unit,
    private val binding: ItemAddCandidateGifticonBinding = ItemAddCandidateGifticonBinding.bind(
        LayoutInflater.from(parent.context).inflate(R.layout.item_add_candidate_gifticon, parent, false)
    )
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: AddGifticonItemUIModel.Gifticon) {
        binding.dm = AddCandidateGifticonDisplayModel(item, onClick, onDelete)
        binding.ivCandidate.loadUriWithoutCache(item.uri)
    }
}
