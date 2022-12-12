package com.lighthouse.presentation.binding

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.net.Uri
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.lighthouse.presentation.R

@BindingAdapter("loadUri")
fun ImageView.loadUri(uri: Uri?) {
    setImageBitmap(null)
    if (uri != null) {
        Glide.with(this)
            .load(uri)
            .into(this)
    } else {
        setImageBitmap(null)
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

@BindingAdapter("filterToGray")
fun ImageView.applyFilterGray(applyFilter: Boolean = true) {
    if (applyFilter) {
        setColorFilter(Color.parseColor("#55000000"), PorterDuff.Mode.DARKEN)
    } else {
        clearColorFilter()
    }
}

@BindingAdapter("loadWithFileStreamPath")
fun ImageView.loadWithFileStreamPath(filename: String?) {
    setImageBitmap(null)
    if (filename != null) {
        val file = context.getFileStreamPath(filename)
        Glide.with(this)
            .load(file)
            .into(this)
    } else {
        setImageBitmap(null)
    }
}

@BindingAdapter("setImageRes")
fun setImageRes(view: ImageView, @DrawableRes resId: Int?) {
    if (resId != null) {
        view.setImageResource(resId)
    } else {
        view.setImageBitmap(null)
    }
}

@BindingAdapter("setTintRes")
fun setTintRes(view: ImageView, @ColorRes resId: Int?) {
    resId ?: return
    view.imageTintList = ColorStateList.valueOf(view.context.getColor(resId))
}

@BindingAdapter("shakeAnimation")
fun playShakeAnimation(view: ImageView, play: Boolean) {
    val shakeAnimation = AnimationUtils.loadAnimation(view.context, R.anim.anim_shake)

    if (play) {
        view.startAnimation(shakeAnimation)
    }
}
