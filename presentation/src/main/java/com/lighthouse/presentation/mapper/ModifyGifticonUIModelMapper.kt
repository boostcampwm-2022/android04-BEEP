package com.lighthouse.presentation.mapper

import androidx.core.graphics.toRect
import com.lighthouse.core.exts.toDigit
import com.lighthouse.domain.model.GifticonForUpdate
import com.lighthouse.presentation.model.ModifyGifticonUIModel

fun ModifyGifticonUIModel.toDomain(): GifticonForUpdate {
    return GifticonForUpdate(
        id = id,
        userId = userId,
        hasImage = hasImage,
        oldCroppedUri = oldCroppedUri.toString(),
        croppedUri = croppedUri.toString(),
        croppedRect = croppedRect.toRect().toDomain(),
        name = name,
        brandName = brandName,
        barcode = barcode,
        expiredAt = expiredAt,
        isCashCard = isCashCard,
        balance = balance.toDigit(),
        memo = memo,
        isUsed = isUsed,
        createdAt = createdAt
    )
}
