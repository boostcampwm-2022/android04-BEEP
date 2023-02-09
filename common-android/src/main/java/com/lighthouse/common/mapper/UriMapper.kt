package com.lighthouse.common.mapper

import android.net.Uri

fun Uri?.toDomain(): String {
    return this?.toString() ?: ""
}

fun String?.toUri(): Uri? {
    return if (!isNullOrEmpty()) {
        try {
            Uri.parse(this)
        } catch (e: Exception) {
            null
        }
    } else {
        null
    }
}
