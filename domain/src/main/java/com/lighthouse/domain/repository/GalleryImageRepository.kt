package com.lighthouse.domain.repository

import androidx.paging.PagingData
import com.lighthouse.domain.model.GalleryImage
import kotlinx.coroutines.flow.Flow

interface GalleryImageRepository {
    fun getImages(): Flow<PagingData<GalleryImage>>
}
