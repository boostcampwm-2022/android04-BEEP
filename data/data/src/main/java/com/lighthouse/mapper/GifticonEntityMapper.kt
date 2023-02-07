package com.lighthouse.mapper

import com.lighthouse.beep.model.gifticon.Gifticon
import com.lighthouse.database.entity.GifticonEntity

fun GifticonEntity.toDomain(): Gifticon {
    return Gifticon(
        id = id,
        createdAt = createdAt,
        userId = userId,
        hasImage = hasImage,
        croppedUri = croppedUri.toString(),
        name = name,
        brand = brand,
        expireAt = expireAt,
        barcode = barcode,
        isCashCard = isCashCard,
        balance = balance,
        memo = memo,
        isUsed = isUsed
    )
}
