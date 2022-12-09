package com.lighthouse.presentation.mapper

import com.lighthouse.domain.model.GalleryImage
import com.lighthouse.presentation.model.AddGifticonUIModel
import com.lighthouse.presentation.model.CroppedImage
import com.lighthouse.presentation.model.GalleryUIModel
import com.lighthouse.presentation.ui.addgifticon.adapter.AddGifticonItemUIModel
import java.util.Date

fun GalleryUIModel.Gallery.toAddGifticonItemUIModel(
    thumbnailImage: CroppedImage = CroppedImage(),
    isDelete: Boolean = false,
    isValid: Boolean = false
): AddGifticonItemUIModel.Gifticon {
    return AddGifticonItemUIModel.Gifticon(
        id = id,
        origin = uri,
        thumbnailImage = thumbnailImage,
        isDelete = isDelete,
        isValid = isValid
    )
}

fun GalleryUIModel.Gallery.toAddGifticonUIModel(
    hasImage: Boolean = true,
    name: String = "",
    brandName: String = "",
    brandConfirm: Boolean = false,
    barcode: String = "",
    expiredAt: Date = Date(0),
    isCashCard: Boolean = false,
    balance: String = "",
    memo: String = "",
    thumbnailImage: CroppedImage = CroppedImage()
): AddGifticonUIModel {
    return AddGifticonUIModel(
        id = id,
        origin = uri,
        hasImage = hasImage,
        name = name,
        brandName = brandName,
        isBrandConfirm = brandConfirm,
        barcode = barcode,
        expiredAt = expiredAt,
        isCashCard = isCashCard,
        balance = balance,
        memo = memo,
        thumbnailImage = thumbnailImage
    )
}

fun GalleryUIModel.Gallery.toDomain(): GalleryImage = GalleryImage(
    id = id,
    contentUri = uri.toString(),
    date = date
)
