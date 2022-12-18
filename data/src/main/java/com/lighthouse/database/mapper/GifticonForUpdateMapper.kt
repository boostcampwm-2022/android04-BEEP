package com.lighthouse.database.mapper

import android.net.Uri
import com.lighthouse.database.entity.GifticonWithCrop
import com.lighthouse.domain.model.GifticonForUpdate
import com.lighthouse.mapper.toEntity

fun GifticonForUpdate.toEntity(): GifticonWithCrop {
    return GifticonWithCrop(
        id = id,
        userId = userId,
        hasImage = hasImage,
        croppedUri = Uri.parse(croppedUri),
        name = name,
        brand = brandName,
        expireAt = expiredAt,
        barcode = barcode,
        isCashCard = isCashCard,
        balance = balance,
        memo = memo,
        croppedRect = croppedRect.toEntity(),
        isUsed = isUsed,
        createdAt = createdAt
    )
}
