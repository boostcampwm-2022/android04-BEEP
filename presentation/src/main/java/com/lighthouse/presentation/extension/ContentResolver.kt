package com.lighthouse.presentation.extension

import android.content.ContentResolver
import android.content.ContentResolver.SCHEME_CONTENT
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Size
import java.io.IOException

fun ContentResolver.getBitmap(uri: Uri): Bitmap? {
    if (uri.scheme != SCHEME_CONTENT) {
        return null
    }
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

fun ContentResolver.getThumbnail(uri: Uri?): Bitmap? {
    if (uri?.scheme != SCHEME_CONTENT) {
        return null
    }

    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val size = screenWidth / 4
        try {
            loadThumbnail(uri, Size(size, size), null)
        } catch (e: Exception) {
            null
        }
    } else {
        uri.lastPathSegment?.toLong()?.let { id ->
            MediaStore.Images.Thumbnails.getThumbnail(this, id, MediaStore.Images.Thumbnails.MINI_KIND, null)
        }
    }
}
