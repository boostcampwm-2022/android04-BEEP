package com.lighthouse.beep.model.gallery

import java.util.Date

data class GalleryImage(
    val id: Long,
    val contentUri: String,
    val date: Date
)
