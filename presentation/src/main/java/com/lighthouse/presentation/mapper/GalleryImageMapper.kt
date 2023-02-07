package com.lighthouse.presentation.mapper

import android.net.Uri
import com.lighthouse.beep.model.gallery.GalleryImage
import com.lighthouse.core.exts.toFormatString
import com.lighthouse.presentation.model.GalleryUIModel

fun GalleryImage.toPresentation(index: Int = -1): GalleryUIModel.Gallery = GalleryUIModel.Gallery(
    id = id,
    uri = Uri.parse(contentUri),
    selectedOrder = index,
    createdDate = date.toFormatString("yyyy-MM-dd")
)
