package com.lighthouse.domain.model

import com.sun.jndi.toolkit.url.Uri
import java.util.Date

data class GifticonSaveRequest(
    val userId: String,
    val name: String,
    val brand: String,
    val expireAt: Date,
    val barcode: String,
    val isCashCard: Boolean,
    val balance: Int,
    val memo: String,
    val thumbnailUri: Uri,
    val originUri: Uri,
    val brandUri: Uri
)
