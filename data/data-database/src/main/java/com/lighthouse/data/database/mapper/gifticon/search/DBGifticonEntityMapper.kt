package com.lighthouse.data.database.mapper.gifticon.search

import com.lighthouse.beep.model.gifticon.Gifticon
import com.lighthouse.data.database.entity.DBGifticonEntity

internal fun List<DBGifticonEntity>.toDomain(): List<Gifticon> {
    return map {
        it.toDomain()
    }
}

internal fun DBGifticonEntity.toDomain(): Gifticon {
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
