package com.lighthouse.database.mapper

import com.lighthouse.database.entity.GifticonEntity
import com.lighthouse.domain.model.GifticonForAddition
import com.lighthouse.domain.util.currentTime
import com.lighthouse.util.UUID

fun GifticonForAddition.toEntity(userId: String): GifticonEntity {
    return GifticonEntity(
        id = UUID.generate(),
        createdAt = currentTime,
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
