package com.lighthouse.presentation.ui.history.adapter.gifticondetail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lighthouse.presentation.R
import com.lighthouse.presentation.databinding.ItemHistoryHeaderBinding
import com.lighthouse.presentation.model.HistoryUiModel

class HistoryHeaderViewHolder(
    parent: ViewGroup,
    private val binding: ItemHistoryHeaderBinding = ItemHistoryHeaderBinding.bind(
        LayoutInflater.from(parent.context).inflate(R.layout.item_history_header, parent, false),
    ),
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: HistoryUiModel.Header) {
        binding.item = item
    }
}
