package com.lighthouse.mapper

import android.net.Uri
import com.lighthouse.beep.model.gifticon.GifticonForUpdate
import com.lighthouse.beep.model.gifticon.GifticonWithCrop
import com.lighthouse.common.mapper.toDomain

internal fun GifticonForUpdate.toGifticonWithCrop(
    newCroppedUri: Uri?
): GifticonWithCrop {
    return GifticonWithCrop(
        id = id,
        userId = userId,
        hasImage = hasImage,
        croppedUri = newCroppedUri.toDomain(),
        name = name,
        brand = brandName,
        expireAt = expiredAt,
        barcode = barcode,
        isCashCard = isCashCard,
        balance = balance,
        memo = memo,
        croppedRect = croppedRect,
        isUsed = isUsed,
        createdAt = createdAt
    )
}
