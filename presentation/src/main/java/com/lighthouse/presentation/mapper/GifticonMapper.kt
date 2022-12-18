package com.lighthouse.presentation.mapper

import android.net.Uri
import com.lighthouse.domain.model.Gifticon
import com.lighthouse.presentation.model.GifticonUiModel

fun Gifticon.toPresentation(distance: Double): GifticonUiModel {
    return GifticonUiModel(
        id = id,
        userId = userId,
        hasImage = hasImage,
        croppedUri = Uri.parse(croppedUri),
        name = name,
        brand = brand,
        expireAt = expireAt,
        balance = balance,
        isUsed = isUsed,
        distance = distance.toInt()
    )
}
