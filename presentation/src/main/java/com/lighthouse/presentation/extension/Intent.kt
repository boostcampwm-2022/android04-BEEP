package com.lighthouse.presentation.extension

import android.content.Intent
import android.os.Build
import android.os.Parcelable

fun <T : Parcelable> Intent.getParcelableArrayList(key: String, clazz: Class<T>): ArrayList<T>? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        extras?.getParcelableArrayList(key, clazz)
    } else {
        extras?.getParcelableArrayList(key)
    }
}
