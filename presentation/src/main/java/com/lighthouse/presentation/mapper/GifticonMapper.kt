package com.lighthouse.presentation.mapper

import android.net.Uri
import com.lighthouse.domain.model.Gifticon
import com.lighthouse.presentation.model.GifticonUIModel
import com.lighthouse.presentation.model.GifticonWithDistanceUIModel

fun Gifticon.toPresentation(distance: Double): GifticonWithDistanceUIModel {
    return GifticonWithDistanceUIModel(
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

fun Gifticon.toPresentation(): GifticonUIModel {
    return GifticonUIModel(
        id = id,
        hasImage = hasImage,
        croppedUri = if (croppedUri.isNotEmpty()) Uri.parse(croppedUri) else null,
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
