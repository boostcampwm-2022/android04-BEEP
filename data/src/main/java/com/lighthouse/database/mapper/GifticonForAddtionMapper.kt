package com.lighthouse.database.mapper

import com.lighthouse.database.entity.GifticonWithCrop
import com.lighthouse.domain.model.GifticonForAddition
import com.lighthouse.mapper.toEntity
import com.lighthouse.model.GifticonImageResult
import java.util.Date

fun GifticonForAddition.toEntity(
    id: String,
    userId: String,
    result: GifticonImageResult?
): GifticonWithCrop {
    return GifticonWithCrop(
        id = id,
        userId = userId,
        hasImage = hasImage,
        croppedUri = result?.outputCroppedUri,
        name = name,
        brand = brandName,
        expireAt = expiredAt,
        barcode = barcode,
        isCashCard = isCashCard,
        balance = balance,
        memo = memo,
        croppedRect = croppedRect.toEntity().apply {
            val sampleSize = result?.sampleSize ?: 1
            set(left / sampleSize, top / sampleSize, right / sampleSize, bottom / sampleSize)
        },
        isUsed = false,
        createdAt = Date()
    )
}
