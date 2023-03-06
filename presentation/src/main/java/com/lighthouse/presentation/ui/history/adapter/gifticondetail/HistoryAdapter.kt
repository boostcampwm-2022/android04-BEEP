package com.lighthouse.presentation.ui.history.adapter.gifticondetail

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lighthouse.presentation.model.HistoryUiModel

class HistoryAdapter : ListAdapter<HistoryUiModel, RecyclerView.ViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            HEADER_TYPE -> HistoryHeaderViewHolder(parent)
            ITEM_TYPE -> HistoryItemViewHolder(parent)
            else -> throw IllegalStateException("Unexpected ViewHolder type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        return when (holder) {
            is HistoryHeaderViewHolder -> holder.bind(currentList[position] as HistoryUiModel.Header)
            is HistoryItemViewHolder -> holder.bind(currentList[position] as HistoryUiModel.History)
            else -> throw IllegalStateException("Unexpected ViewHolder type")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (currentList[position]) {
            is HistoryUiModel.Header -> HEADER_TYPE
            else -> ITEM_TYPE
        }
    }

    companion object {
        const val HEADER_TYPE = 0
        const val ITEM_TYPE = 1

        private val diffUtil = object : DiffUtil.ItemCallback<HistoryUiModel>() {
            override fun areItemsTheSame(oldItem: HistoryUiModel, newItem: HistoryUiModel): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: HistoryUiModel, newItem: HistoryUiModel): Boolean {
                return oldItem == newItem
            }
        }
    }
}
