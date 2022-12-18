package com.lighthouse.presentation.mapper

import android.graphics.RectF
import com.lighthouse.domain.model.GalleryImage
import com.lighthouse.presentation.extension.toDate
import com.lighthouse.presentation.model.AddGifticonUIModel
import com.lighthouse.presentation.model.CroppedImage
import com.lighthouse.presentation.model.GalleryUIModel
import com.lighthouse.presentation.ui.edit.addgifticon.adapter.AddGifticonItemUIModel
import java.util.Date

fun GalleryUIModel.Gallery.toAddGifticonItemUIModel(): AddGifticonItemUIModel.Gifticon {
    return AddGifticonItemUIModel.Gifticon(
        id = id,
        origin = uri,
        thumbnailImage = CroppedImage(),
        isSelected = false,
        isDelete = false,
        isValid = false
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
        approveExpiredAt = false,
        isCashCard = false,
        balance = "",
        balanceRectF = RectF(),
        memo = "",
        gifticonImage = CroppedImage(),
        approveGifticonImage = false,
        createdDate = createdDate
    )
}

fun GalleryUIModel.Gallery.toDomain(): GalleryImage = GalleryImage(
    id = id,
    contentUri = uri.toString(),
    date = createdDate.toDate("yyyy-MM-dd")
)
