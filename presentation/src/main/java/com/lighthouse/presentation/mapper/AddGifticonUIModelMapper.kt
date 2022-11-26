package com.lighthouse.presentation.mapper

import android.graphics.RectF
import com.lighthouse.presentation.model.AddGifticonUIModel
import com.lighthouse.presentation.model.GalleryUIModel
import java.util.Date

fun GalleryUIModel.Gallery.toAddGifticonModel(): AddGifticonUIModel.Gifticon {
    return AddGifticonUIModel.Gifticon(
        id = id,
        origin = uri,
        name = "",
        brandName = "",
        expiredAt = Date(),
        isCashCard = false,
        balance = "",
        memo = "",
        brandUri = null,
        brandRect = RectF(),
        cropped = null,
        cropRect = RectF(),
        isDelete = false,
        isValid = false
    )
}
