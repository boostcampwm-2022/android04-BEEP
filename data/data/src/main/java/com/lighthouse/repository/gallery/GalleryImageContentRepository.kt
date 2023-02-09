package com.lighthouse.repository.gallery

import androidx.paging.PagingData
import com.lighthouse.beep.model.gallery.GalleryImage
import kotlinx.coroutines.flow.Flow

interface GalleryImageContentRepository {

    fun getImages(pageSize: Int): Flow<PagingData<GalleryImage>>
}
