package com.lighthouse.presentation.extension

import android.content.Intent
import android.os.Parcelable

fun <T : Parcelable> Intent.getParcelableArrayList(key: String, clazz: Class<T>): ArrayList<T>? {
    return extras?.getParcelableArrayListCompat(key, clazz)
}

fun <T : Parcelable> Intent.getParcelable(key: String, clazz: Class<T>): T? {
    return extras?.getParcelableCompat(key, clazz)
}
