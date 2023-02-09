package com.lighthouse.mapper

import androidx.core.net.toUri
import com.lighthouse.beep.model.gifticon.GifticonForAddition
import com.lighthouse.beep.model.gifticon.GifticonWithCrop
import com.lighthouse.common.mapper.toDomain
import com.lighthouse.model.gifticon.GifticonImageResult
import java.util.Date

internal fun GifticonForAddition.toGifticonWithCrop(
    userId: String,
    gifticonId: String,
    result: GifticonImageResult?
): GifticonWithCrop {
    return GifticonWithCrop(
        id = gifticonId,
        userId = userId,
        hasImage = hasImage,
        croppedUri = result?.croppedFile?.toUri().toDomain(),
        name = name,
        brand = brandName,
        expireAt = expiredAt,
        barcode = barcode,
        isCashCard = isCashCard,
        balance = balance,
        memo = memo,
        croppedRect = croppedRect.sampling(result?.sampleSize),
        isUsed = false,
        createdAt = Date()
    )
}
