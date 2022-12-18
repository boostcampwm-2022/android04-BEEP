package com.lighthouse.presentation.model

import android.net.Uri
import java.util.Date

data class GifticonUiModel(
    val id: String,
    val userId: String,
    val hasImage: Boolean,
    val croppedUri: Uri,
    val name: String,
    val brand: String,
    val expireAt: Date,
    val balance: Int,
    val isUsed: Boolean,
    val distance: Int
)
