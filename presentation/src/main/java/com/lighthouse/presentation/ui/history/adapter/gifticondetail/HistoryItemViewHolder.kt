package com.lighthouse.presentation.ui.history.adapter.gifticondetail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lighthouse.presentation.R
import com.lighthouse.presentation.databinding.ItemHistoryBinding
import com.lighthouse.presentation.model.HistoryUiModel

class HistoryItemViewHolder(
    parent: ViewGroup,
    private val binding: ItemHistoryBinding = ItemHistoryBinding.bind(
        LayoutInflater.from(parent.context).inflate(R.layout.item_history, parent, false),
    ),
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: HistoryUiModel.History) {
        binding.history = item
    }
}
