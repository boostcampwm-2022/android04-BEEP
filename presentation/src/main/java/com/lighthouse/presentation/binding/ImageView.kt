package com.lighthouse.presentation.binding

import android.net.Uri
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.lighthouse.presentation.extension.getThumbnail

@BindingAdapter("loadUri")
fun loadUri(view: ImageView, uri: Uri?) {
    view.setImageBitmap(null)
    if (uri != null) {
        Glide.with(view)
            .load(uri)
            .centerCrop()
            .into(view)
    } else {
        view.setImageBitmap(null)
    }
}

@BindingAdapter("loadUriWithoutCache")
fun ImageView.loadUriWithoutCache(uri: Uri?) {
    setImageBitmap(null)
    if (uri != null) {
        Glide.with(this)
            .load(uri)
            .skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .into(this)
    } else {
        setImageBitmap(null)
    }
}

@BindingAdapter("loadThumbnailByContentUri")
fun loadThumbnailByContentUri(view: ImageView, contentUri: Uri?) {
    val resolver = view.context.contentResolver
    val thumbnail = resolver.getThumbnail(contentUri)
    view.setImageBitmap(null)
    if (thumbnail != null) {
        Glide.with(view)
            .load(thumbnail)
            .centerCrop()
            .into(view)
    } else {
        if (contentUri != null) {
            Glide.with(view)
                .load(contentUri)
                .centerCrop()
                .into(view)
        } else {
            view.setImageBitmap(null)
        }
    }
}
