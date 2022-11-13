package com.lighthouse.presentation.binding

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lighthouse.presentation.adapter.BindableAdapter

@BindingAdapter("items")
fun <T> setData(view: RecyclerView, data: T?) {
    data ?: return
    when (val listAdapter = view.adapter) {
        is BindableAdapter<*> -> {
            (listAdapter as BindableAdapter<T>).setData(data)
        }
    }
}
