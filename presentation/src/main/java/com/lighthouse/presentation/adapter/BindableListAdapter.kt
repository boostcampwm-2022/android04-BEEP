package com.lighthouse.presentation.adapter

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

abstract class BindableListAdapter<T, VH : RecyclerView.ViewHolder>(diffCallback: DiffUtil.ItemCallback<T>) :
    ListAdapter<T, VH>(diffCallback),
    BindableAdapter<List<T>> {

    override fun setData(data: List<T>, commitCallback: () -> Unit) {
        submitList(data, commitCallback)
    }
}
