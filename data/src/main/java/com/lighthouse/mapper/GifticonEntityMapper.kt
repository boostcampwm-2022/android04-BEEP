package com.lighthouse.mapper

import com.lighthouse.database.entity.GifticonEntity
import com.lighthouse.domain.model.Gifticon

fun GifticonEntity.toGifticon(): Gifticon {
    return Gifticon(
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
