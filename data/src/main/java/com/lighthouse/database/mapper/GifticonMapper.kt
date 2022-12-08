package com.lighthouse.database.mapper

import com.lighthouse.database.entity.GifticonEntity
import com.lighthouse.domain.model.Gifticon

fun Gifticon.toEntity(): GifticonEntity {
    return GifticonEntity(
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
