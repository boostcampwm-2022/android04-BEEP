package com.lighthouse.datasource

import com.lighthouse.domain.model.GalleryImage

interface GalleryImageLocalSource {
    suspend fun getImages(): List<GalleryImage>
}
