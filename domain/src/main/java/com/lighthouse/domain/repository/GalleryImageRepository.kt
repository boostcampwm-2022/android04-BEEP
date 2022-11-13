package com.lighthouse.domain.repository

import com.lighthouse.domain.model.GalleryImage

interface GalleryImageRepository {
    suspend fun getImages(): List<GalleryImage>
}
