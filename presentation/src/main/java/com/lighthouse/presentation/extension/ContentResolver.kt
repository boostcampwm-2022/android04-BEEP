package com.lighthouse.presentation.extension

import android.content.ContentResolver
import android.content.ContentResolver.SCHEME_CONTENT
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import java.io.IOException

fun ContentResolver.getBitmap(uri: Uri): Bitmap? {
    if (uri.scheme != SCHEME_CONTENT) {
        return null
    }
    return try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(this, uri)
            ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
                decoder.isMutableRequired = true
            }
        } else {
            MediaStore.Images.Media.getBitmap(this, uri)
        }
    } catch (e: IOException) {
        null
    }
}
