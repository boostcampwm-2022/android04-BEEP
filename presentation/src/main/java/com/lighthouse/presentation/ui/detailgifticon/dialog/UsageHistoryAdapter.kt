package com.lighthouse.presentation.ui.detailgifticon.dialog

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.lighthouse.domain.model.History
import com.lighthouse.presentation.R
import com.lighthouse.presentation.adapter.BindableListAdapter
import com.lighthouse.presentation.databinding.ItemUsageHistoryBinding
import com.lighthouse.presentation.util.Geography

class UsageHistoryAdapter : BindableListAdapter<History, UsageHistoryAdapter.UsageHistoryViewHolder>(diffUtil) {

    class UsageHistoryViewHolder(private val binding: ItemUsageHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(usageHistory: History) {
            binding.geo = Geography(binding.root.context)
//            binding.usage = usageHistory
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsageHistoryViewHolder {
        val view = DataBindingUtil.inflate<ItemUsageHistoryBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_usage_history,
            parent,
            false,
        )
        return UsageHistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: UsageHistoryViewHolder, position: Int) {
        return holder.bind(currentList[position])
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<History>() {
            override fun areItemsTheSame(oldItem: History, newItem: History): Boolean {
                return oldItem.date == newItem.date
            }

            override fun areContentsTheSame(oldItem: History, newItem: History): Boolean {
                return oldItem == newItem
            }
        }
    }
}
