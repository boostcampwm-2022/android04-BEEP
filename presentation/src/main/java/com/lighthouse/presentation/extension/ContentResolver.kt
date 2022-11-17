package com.lighthouse.presentation.extension

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import java.io.IOException

fun ContentResolver.getBitmap(uri: Uri): Bitmap? {
    return try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            ImageDecoder.decodeBitmap(ImageDecoder.createSource(this, uri))
        } else {
            MediaStore.Images.Media.getBitmap(this, uri)
        }
    } catch (e: IOException) {
        null
    }
}
