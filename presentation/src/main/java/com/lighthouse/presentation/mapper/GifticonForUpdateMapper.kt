package com.lighthouse.presentation.mapper

import android.graphics.RectF
import android.net.Uri
import androidx.core.graphics.toRectF
import com.lighthouse.domain.model.GifticonForUpdate
import com.lighthouse.presentation.model.ModifyGifticonUIModel

fun GifticonForUpdate.toPresentation(): ModifyGifticonUIModel {
    return ModifyGifticonUIModel(
        id = id,
        userId = userId,
        hasImage = hasImage,
        oldCroppedUri = Uri.parse(oldCroppedUri),
        croppedUri = Uri.parse(croppedUri),
        croppedRect = croppedRect.toPresentation().toRectF(),
        name = name,
        nameRectF = RectF(),
        brandName = brandName,
        brandNameRectF = RectF(),
        approveBrandName = brandName,
        barcode = barcode,
        barcodeRectF = RectF(),
        expiredAt = expiredAt,
        expiredAtRectF = RectF(),
        approveExpiredAt = false,
        isCashCard = isCashCard,
        balance = balance.toString(),
        balanceRectF = RectF(),
        memo = memo,
        isUsed = isUsed,
        createdAt = createdAt
    )
}
