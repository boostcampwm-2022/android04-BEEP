package com.lighthouse.presentation.ui.history.adapter.gifticondetail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lighthouse.presentation.R
import com.lighthouse.presentation.databinding.ItemHistoryBinding
import com.lighthouse.presentation.model.HistoryUiModel
import com.lighthouse.presentation.util.resource.UIText

class HistoryItemViewHolder(
    parent: ViewGroup,
    private val binding: ItemHistoryBinding = ItemHistoryBinding.bind(
        LayoutInflater.from(parent.context).inflate(R.layout.item_history, parent, false),
    ),
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: HistoryUiModel.History) {
        binding.history = item
        val chipBackground = when (item.type) {
            UIText.StringResource(R.string.history_type_init) -> R.drawable.bg_history_init
            UIText.StringResource(R.string.history_type_use) -> R.drawable.bg_history_use
            UIText.StringResource(R.string.history_type_cancel) -> R.drawable.bg_history_cancel
            UIText.StringResource(R.string.history_type_modify_balance) -> R.drawable.bg_history_modify
            else -> throw IllegalStateException("Unknown history type")
        }
        binding.tvHistoryType.setBackgroundResource(chipBackground)
    }
}
