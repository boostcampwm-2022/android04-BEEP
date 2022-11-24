package com.lighthouse.presentation.binding

import android.net.Uri
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.lighthouse.presentation.extension.getThumbnail

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

@BindingAdapter("loadThumbnailByContentUri")
fun loadThumbnailByContentUri(view: ImageView, contentUri: Uri?) {
    val resolver = view.context.contentResolver
    val thumbnail = resolver.getThumbnail(contentUri)
    if (thumbnail != null) {
        Glide.with(view)
            .load(thumbnail)
            .centerCrop()
            .into(view)
    } else {
        view.setImageBitmap(null)
    }
}
