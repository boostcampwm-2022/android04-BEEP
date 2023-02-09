package com.lighthouse.beep.model.gifticon

import java.io.File

data class GifticonImageResult(
    val sampleSize: Int,
    val originFile: File,
    val croppedFile: File
)
