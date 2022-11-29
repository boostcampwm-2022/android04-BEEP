package com.lighthouse.database.mapper

import com.lighthouse.database.entity.GifticonEntity
import com.lighthouse.domain.model.Gifticon
import com.lighthouse.domain.model.GifticonForAddition
import com.lighthouse.util.UUID

fun Gifticon.toGifticonEntity(): GifticonEntity {
    return GifticonEntity(
        id = id,
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

fun GifticonForAddition.toEntity(userId: String): GifticonEntity {
    return GifticonEntity(
        id = UUID.generate(),
        userId = userId,
        hasImage = hasImage,
        name = name,
        brand = brandName,
        expireAt = expiredAt,
        barcode = barcode,
        isCashCard = isCashCard,
        balance = balance,
        memo = memo,
        false
    )
}
