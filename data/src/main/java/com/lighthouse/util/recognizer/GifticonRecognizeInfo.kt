package com.lighthouse.util.recognizer

import android.graphics.Bitmap
import android.graphics.Rect
import java.util.Date

data class GifticonRecognizeInfo(
    val name: String = "",
    val brand: String = "",
    val expiredAt: Date = Date(0),
    val barcode: String = "",
    val isCashCard: Boolean = false,
    val balance: Int = 0,
    val candidate: List<String> = listOf(),
    val croppedImage: Bitmap? = null,
    val croppedRect: Rect = Rect()
)
