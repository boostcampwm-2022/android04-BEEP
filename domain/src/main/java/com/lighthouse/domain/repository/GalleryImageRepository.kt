package com.lighthouse.domain.repository

import androidx.paging.PagingData
import com.lighthouse.domain.model.GalleryImage
import kotlinx.coroutines.flow.Flow

interface GalleryImageRepository {
    fun getImages(pageSize: Int = 10): Flow<PagingData<GalleryImage>>
}
