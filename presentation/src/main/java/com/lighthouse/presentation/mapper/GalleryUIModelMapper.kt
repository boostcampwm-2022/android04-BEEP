package com.lighthouse.presentation.mapper

import android.net.Uri
import com.lighthouse.domain.model.GalleryImage
import com.lighthouse.presentation.model.AddGifticonUIModel
import com.lighthouse.presentation.model.GalleryUIModel

fun GalleryImage.toPresentation(index: Int = -1): GalleryUIModel.Gallery = GalleryUIModel.Gallery(
    id = id,
    uri = Uri.parse(contentUri),
    selectedOrder = index,
    date = date
)

fun AddGifticonUIModel.toGalleryUIModel(order: Int): GalleryUIModel.Gallery = GalleryUIModel.Gallery(
    id = id,
    uri = origin,
    selectedOrder = order,
    date = expiredAt
)

fun GalleryUIModel.Gallery.toDomain(): GalleryImage = GalleryImage(
    id = id,
    contentUri = uri.toString(),
    date = date
)
