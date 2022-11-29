package com.lighthouse.presentation.adapter

interface BindableAdapter<T> {

    fun setData(data: T, commitCallback: () -> Unit = {})
}
