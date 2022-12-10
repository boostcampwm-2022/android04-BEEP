package com.lighthouse.presentation.mapper

import android.graphics.RectF
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

fun GalleryUIModel.Gallery.toAddGifticonUIModel(): AddGifticonUIModel {
    return AddGifticonUIModel(
        id = id,
        origin = uri,
        hasImage = true,
        name = "",
        nameRectF = RectF(),
        brandName = "",
        brandNameRectF = RectF(),
        approveBrandName = "",
        barcode = "",
        barcodeRectF = RectF(),
        expiredAt = Date(0),
        expiredAtRectF = RectF(),
        isCashCard = false,
        balance = "",
        balanceRectF = RectF(),
        memo = "",
        gifticonImage = CroppedImage(),
        approveGifticonImage = false
    )
}

fun GalleryUIModel.Gallery.toDomain(): GalleryImage = GalleryImage(
    id = id,
    contentUri = uri.toString(),
    date = date
)
