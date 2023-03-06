package com.lighthouse.presentation.mapper

import androidx.core.graphics.toRect
import com.lighthouse.domain.model.GifticonForAddition
import com.lighthouse.presentation.model.AddGifticonUIModel
import com.lighthouse.presentation.model.GalleryUIModel

fun AddGifticonUIModel.toGalleryUIModel(order: Int): GalleryUIModel.Gallery = GalleryUIModel.Gallery(
    id = id,
    uri = origin,
    selectedOrder = order,
    createdDate = createdDate,
)

fun AddGifticonUIModel.toDomain(): GifticonForAddition {
    return GifticonForAddition(
        hasImage = hasImage,
        name = name,
        brandName = brandName,
        barcode = barcode,
        expiredAt = expiredAt,
        isCashCard = isCashCard,
        balance = if (isCashCard) balance.toIntOrNull() else null,
        memo = memo,
        originUri = origin.toString(),
        tempCroppedUri = gifticonImage.uri?.toString() ?: "",
        croppedRect = gifticonImage.croppedRect.toRect().toDomain(),
    )
}
