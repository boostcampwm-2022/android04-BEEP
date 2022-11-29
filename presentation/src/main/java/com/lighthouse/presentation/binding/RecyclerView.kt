package com.lighthouse.presentation.binding

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.lighthouse.presentation.adapter.BindableAdapter

@BindingAdapter("setItems")
fun <T> setItems(view: RecyclerView, data: T?) {
    data ?: return
    when (val listAdapter = view.adapter) {
        is BindableAdapter<*> -> {
            (listAdapter as BindableAdapter<T>).setData(data) {
                view.invalidateItemDecorations()
            }
        }
    }
}

@BindingAdapter("setViewPagerItems")
fun <T> setViewPagerItems(view: ViewPager2, data: T?) {
    data ?: return
    when (val listAdapter = view.adapter) {
        is BindableAdapter<*> -> {
            (listAdapter as BindableAdapter<T>).setData(data) {
                view.invalidateItemDecorations()
            }
        }
    }
}
