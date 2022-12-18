package com.lighthouse.database.mapper

import android.net.Uri
import com.lighthouse.database.entity.GifticonWithCrop
import com.lighthouse.domain.model.GifticonForAddition
import com.lighthouse.mapper.toEntity
import java.util.Date

fun GifticonForAddition.toEntity(id: String, userId: String, croppedUri: Uri?): GifticonWithCrop {
    return GifticonWithCrop(
        id = id,
        userId = userId,
        hasImage = hasImage,
        croppedUri = croppedUri,
        name = name,
        brand = brandName,
        expireAt = expiredAt,
        barcode = barcode,
        isCashCard = isCashCard,
        balance = balance,
        memo = memo,
        croppedRect = croppedRect.toEntity(),
        isUsed = false,
        createdAt = Date()
    )
}
