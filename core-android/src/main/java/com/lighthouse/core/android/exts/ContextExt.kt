package com.lighthouse.core.android.exts

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.core.net.toFile
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream

fun Context.openInputStream(uri: Uri): InputStream {
    return contentResolver.openInputStream(uri)
        ?: throw FileNotFoundException("파일을 찾을 수 없어 InputStream 을 열 수 없습니다.")
}

fun Context.calculateSampleSize(uri: Uri): Int {
    val stream = openInputStream(uri)
    val options = BitmapFactory.Options().apply {
        inJustDecodeBounds = true
    }
    BitmapFactory.decodeStream(stream, null, options)
    val imageWidth = options.outWidth
    val imageHeight = options.outHeight
    var inSampleSize = 1

    while (imageHeight / inSampleSize > screenHeight || imageWidth / inSampleSize > screenWidth) {
        inSampleSize *= 2
    }
    return inSampleSize
}

@Suppress("DEPRECATION")
fun Context.decodeBitmap(uri: Uri): Bitmap {
    return when (uri.scheme) {
        ContentResolver.SCHEME_CONTENT -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(contentResolver, uri)
            ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
                decoder.isMutableRequired = true
            }
        } else {
            MediaStore.Images.Media.getBitmap(contentResolver, uri)
        }

        ContentResolver.SCHEME_FILE -> BitmapFactory.decodeFile(uri.path)
        else -> throw IOException("알 수 없는 scheme 입니다.")
    }
}

fun Context.decodeSampledBitmap(uri: Uri, sampleSize: Int): Bitmap {
    val options = BitmapFactory.Options().apply {
        inSampleSize = sampleSize
    }
    return when (uri.scheme) {
        ContentResolver.SCHEME_CONTENT -> BitmapFactory.decodeStream(
            openInputStream(uri),
            null,
            options
        ) ?: throw FileNotFoundException("파일을 찾을 수 없습니다.")

        ContentResolver.SCHEME_FILE -> BitmapFactory.decodeFile(uri.path)
        else -> throw IOException("알 수 없는 scheme 입니다.")
    }
}

fun Context.deleteFile(uri: Uri) {
    when (uri.scheme) {
        ContentResolver.SCHEME_CONTENT -> contentResolver.delete(uri, null, null)
        ContentResolver.SCHEME_FILE -> uri.toFile().delete()
        else -> throw IOException("알 수 없는 scheme 입니다.")
    }
}

fun Context.exists(uri: Uri): Boolean {
    return when (uri.scheme) {
        ContentResolver.SCHEME_CONTENT -> {
            contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                cursor.moveToFirst()
            } ?: false
        }

        ContentResolver.SCHEME_FILE -> uri.toFile().exists()
        else -> throw IOException("알 수 없는 scheme 입니다.")
    }
}
