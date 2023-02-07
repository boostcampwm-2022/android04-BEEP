package com.lighthouse.datasource.gallery

import com.lighthouse.beep.model.gallery.GalleryImage

interface GalleryImageLocalSource {
    suspend fun getImages(page: Int, limit: Int): List<GalleryImage>
}
