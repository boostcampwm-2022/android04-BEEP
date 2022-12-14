package com.lighthouse.presentation.mapper

import android.net.Uri
import com.lighthouse.domain.model.GalleryImage
import com.lighthouse.presentation.extension.toString
import com.lighthouse.presentation.model.GalleryUIModel

fun GalleryImage.toPresentation(index: Int = -1): GalleryUIModel.Gallery = GalleryUIModel.Gallery(
    id = id,
    uri = Uri.parse(contentUri),
    selectedOrder = index,
    createdDate = date.toString("yyyy-MM-dd")
)
