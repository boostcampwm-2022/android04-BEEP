package com.lighthouse.database.mapper

import com.lighthouse.database.entity.GifticonWithCrop
import com.lighthouse.domain.model.GifticonForUpdate
import com.lighthouse.mapper.toDomain

fun GifticonWithCrop.toDomain(): GifticonForUpdate {
    return GifticonForUpdate(
        id = id,
        userId = userId,
        hasImage = hasImage,
        name = name,
        brandName = brand,
        barcode = barcode,
        expiredAt = expireAt,
        isCashCard = isCashCard,
        balance = balance,
        oldCroppedUri = croppedUri.toString(),
        croppedUri = croppedUri.toString(),
        croppedRect = croppedRect.toDomain(),
        memo = memo
    )
}
