package com.lighthouse.mapper

import com.lighthouse.database.entity.GifticonEntity
import com.lighthouse.domain.model.Gifticon

fun GifticonEntity.toDomain(): Gifticon {
    return Gifticon(
        id = id,
        createdAt = createdAt,
        userId = userId,
        hasImage = hasImage,
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
