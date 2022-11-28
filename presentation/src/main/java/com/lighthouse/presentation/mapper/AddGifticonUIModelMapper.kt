package com.lighthouse.presentation.mapper

import com.lighthouse.presentation.model.AddGifticonUIModel
import com.lighthouse.presentation.model.CroppedImage
import com.lighthouse.presentation.model.EditTextInfo
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
    name: String = "",
    brandName: String = "",
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
        name = name,
        brandName = brandName,
        barcode = EditTextInfo(barcode, barcode.length),
        expiredAt = expiredAt,
        isCashCard = isCashCard,
        balance = EditTextInfo(balance, balance.length),
        memo = memo,
        thumbnailImage = thumbnailImage
    )
}
