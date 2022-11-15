package com.lighthouse.presentation.binding

import android.net.Uri
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide

@BindingAdapter("loadUri")
fun loadUri(view: ImageView, uri: Uri?) {
    if (uri != null) {
        Glide.with(view)
            .load(uri)
            .centerCrop()
            .into(view)
    } else {
        view.setImageBitmap(null)
    }
}
