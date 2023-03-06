package com.lighthouse.presentation.model

import android.net.Uri
import java.io.Serializable
import java.util.Date

data class GifticonUIModel(
    val id: String,
    val hasImage: Boolean,
    val croppedUri: Uri?,
    val name: String,
    val brand: String,
    val expireAt: Date,
    val barcode: String,
    val isCashCard: Boolean,
    val balance: Int?,
    val memo: String,
    val isUsed: Boolean,
) : Serializable {

    val originPath = "origin$id"
    val brandLowerName = brand.lowercase()
}
