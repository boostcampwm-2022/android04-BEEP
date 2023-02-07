package com.lighthouse.core.android.exts

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import java.io.Serializable

@Suppress("DEPRECATION")
inline fun <reified T : Parcelable> Intent?.getRequiredParcelableArrayListExtra(
    key: String
): ArrayList<T> {
    this ?: return ArrayList()
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        extras?.getParcelableArrayList(key, T::class.java) ?: ArrayList()
    } else {
        extras?.getParcelableArrayList(key) ?: ArrayList()
    }
}

@Suppress("DEPRECATION")
inline fun <reified T : Serializable> Intent.getRequiredSerializable(
    name: String
): T = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
    requireNotNull(getSerializableExtra(name, T::class.java))
} else {
    getSerializableExtra(name) as T
}

@Suppress("DEPRECATION")
inline fun <reified T : Serializable> Intent?.getSerializable(
    name: String
): T? {
    this ?: return null
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getSerializableExtra(name, T::class.java)
    } else {
        getSerializableExtra(name) as? T
    }
}

@Suppress("DEPRECATION")
inline fun <reified T : Parcelable> Intent.getRequiredParcelableExtra(
    name: String
): T = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
    requireNotNull(getParcelableExtra(name, T::class.java))
} else {
    requireNotNull(getParcelableExtra(name)) as T
}

@Suppress("DEPRECATION")
inline fun <reified T : Parcelable> Intent?.getParcelableExtra(
    name: String
): T? {
    this ?: return null
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getParcelableExtra(name, T::class.java)
    } else {
        getParcelableExtra(name) as? T
    }
}

@Suppress("DEPRECATION")
inline fun <reified T : Serializable> Bundle.getRequiredSerializableExtra(
    name: String
): T = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
    requireNotNull(getSerializable(name, T::class.java))
} else {
    getSerializable(name) as T
}

@Suppress("DEPRECATION")
inline fun <reified T : Serializable> Bundle?.getSerializableExtra(
    name: String
): T? {
    this ?: return null
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getSerializable(name, T::class.java)
    } else {
        getSerializable(name) as? T
    }
}

@Suppress("DEPRECATION")
inline fun <reified T : Parcelable> Bundle.getRequiredParcelableExtra(
    name: String
): T = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
    requireNotNull(getParcelable(name, T::class.java))
} else {
    requireNotNull(getParcelable(name)) as T
}

@Suppress("DEPRECATION")
inline fun <reified T : Parcelable> Bundle?.getParcelableExtra(
    name: String
): T? {
    this ?: return null
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getParcelable(name, T::class.java)
    } else {
        getParcelable(name) as? T
    }
}
