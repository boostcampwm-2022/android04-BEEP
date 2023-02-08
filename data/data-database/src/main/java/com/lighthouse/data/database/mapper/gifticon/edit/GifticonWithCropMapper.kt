package com.lighthouse.data.database.mapper.gifticon.edit

import com.lighthouse.beep.model.gifticon.GifticonWithCrop
import com.lighthouse.common.mapper.toRect
import com.lighthouse.common.mapper.toUri
import com.lighthouse.data.database.model.DBGifticonWithCrop

internal fun List<GifticonWithCrop>.toEntity(): List<DBGifticonWithCrop> {
    return map {
        it.toEntity()
    }
}

internal fun GifticonWithCrop.toEntity(): DBGifticonWithCrop {
    return DBGifticonWithCrop(
        id,
        userId,
        hasImage,
        croppedUri.toUri(),
        name,
        brand,
        expireAt,
        barcode,
        isCashCard,
        balance,
        memo,
        croppedRect.toRect(),
        isUsed,
        createdAt
    )
}
