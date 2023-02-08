package com.lighthouse.data.database.mapper.gifticon.edit

import com.lighthouse.beep.model.gifticon.GifticonWithCrop
import com.lighthouse.common.mapper.toDomain
import com.lighthouse.data.database.entity.DBGifticonCropEntity
import com.lighthouse.data.database.entity.DBGifticonEntity
import com.lighthouse.data.database.model.DBGifticonWithCrop

internal fun DBGifticonWithCrop.toDomain(): GifticonWithCrop {
    return GifticonWithCrop(
        id,
        userId,
        hasImage,
        croppedUri.toDomain(),
        name,
        brand,
        expireAt,
        barcode,
        isCashCard,
        balance,
        memo,
        croppedRect.toDomain(),
        isUsed,
        createdAt
    )
}

internal fun DBGifticonWithCrop.toGifticonEntity(): DBGifticonEntity {
    return DBGifticonEntity(
        id = id,
        userId = userId,
        hasImage = hasImage,
        croppedUri = croppedUri,
        name = name,
        brand = brand,
        expireAt = expireAt,
        barcode = barcode,
        isCashCard = isCashCard,
        balance = balance,
        memo = memo,
        isUsed = isUsed,
        createdAt = createdAt
    )
}

internal fun DBGifticonWithCrop.toGifticonCropEntity(): DBGifticonCropEntity {
    return DBGifticonCropEntity(
        gifticonId = id,
        croppedRect = croppedRect
    )
}
