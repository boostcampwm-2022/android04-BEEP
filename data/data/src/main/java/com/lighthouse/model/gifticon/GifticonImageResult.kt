package com.lighthouse.model.gifticon

import java.io.File

internal data class GifticonImageResult(
    val sampleSize: Int,
    val originFile: File,
    val croppedFile: File
)
