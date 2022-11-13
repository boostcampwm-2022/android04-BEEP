package com.lighthouse.presentation.adapter

import androidx.paging.PagingData
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

abstract class BindablePagingAdapter<T : Any, VH : RecyclerView.ViewHolder>(
    diffCallback: DiffUtil.ItemCallback<T>
) : PagingDataAdapter<T, VH>(diffCallback), BindableAdapter<PagingData<T>> {

    private val scope = CoroutineScope(Dispatchers.Main)

    override fun setData(data: PagingData<T>) {
        scope.launch {
            submitData(data)
        }
    }
}
