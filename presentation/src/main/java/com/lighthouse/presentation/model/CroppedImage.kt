package com.lighthouse.presentation.model

import android.graphics.RectF
import android.net.Uri

data class CroppedImage(
    val uri: Uri? = null,
    val croppedRect: RectF = RectF()
)
