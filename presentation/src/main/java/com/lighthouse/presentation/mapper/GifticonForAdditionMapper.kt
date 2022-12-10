package com.lighthouse.presentation.mapper

import android.graphics.RectF
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
        nameRectF = RectF(),
        brandName = brandName,
        brandNameRectF = RectF(),
        approveBrandName = "",
        barcode = barcode,
        barcodeRectF = RectF(),
        expiredAt = expiredAt,
        expiredAtRectF = RectF(),
        isCashCard = isCashCard,
        balance = balance.toString(),
        balanceRectF = RectF(),
        memo = memo,
        origin = Uri.parse(originUri),
        gifticonImage = CroppedImage(
            uri = if (croppedUri != "") Uri.parse(croppedUri) else null,
            croppedRect = croppedRect.toPresentation().toRectF()
        ),
        approveGifticonImage = false
    )
}
