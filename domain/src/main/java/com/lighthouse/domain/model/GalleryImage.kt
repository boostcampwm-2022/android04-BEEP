package com.lighthouse.domain.model

import com.sun.jndi.toolkit.url.Uri
import java.util.Date

data class GalleryImage(
    val id: Long,
    val contentUri: Uri,
    val date: Date
)
