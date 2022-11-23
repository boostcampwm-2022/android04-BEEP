package com.lighthouse.database.mapper

import com.lighthouse.database.entity.GifticonEntity
import com.lighthouse.domain.model.Gifticon

fun Gifticon.toGifticonEntity(): GifticonEntity {
    return GifticonEntity(
        id = id,
        userId = userId,
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
