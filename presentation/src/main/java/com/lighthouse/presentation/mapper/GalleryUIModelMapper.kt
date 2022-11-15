package com.lighthouse.presentation.mapper

import android.net.Uri
import com.lighthouse.domain.model.GalleryImage
import com.lighthouse.presentation.model.GalleryUIModel

fun GalleryImage.toPresentation(): GalleryUIModel.Gallery = GalleryUIModel.Gallery(
    id = id,
    uri = Uri.parse(contentUri),
    date = date
)
