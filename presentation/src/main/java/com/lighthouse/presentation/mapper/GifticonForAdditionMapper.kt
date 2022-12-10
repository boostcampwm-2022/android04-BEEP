package com.lighthouse.presentation.mapper

import android.net.Uri
import androidx.core.graphics.toRectF
import com.lighthouse.domain.model.GifticonForAddition
import com.lighthouse.presentation.model.AddGifticonUIModel
import com.lighthouse.presentation.model.CroppedImage

fun GifticonForAddition.toPresentation(id: Long): AddGifticonUIModel {
    return AddGifticonUIModel(
        id = id,
        hasImage = hasImage,
        name = name,
        brandName = brandName,
        isBrandConfirm = false,
        barcode = barcode,
        expiredAt = expiredAt,
        isCashCard = isCashCard,
        balance = balance.toString(),
        memo = memo,
        origin = Uri.parse(originUri),
        thumbnailImage = CroppedImage(
            uri = if (croppedUri != "") Uri.parse(croppedUri) else null,
            croppedRect = croppedRect.toPresentation().toRectF()
        )
    )
}
