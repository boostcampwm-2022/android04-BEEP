package com.lighthouse.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.lighthouse.datasource.gallery.GalleryImageLocalSource
import com.lighthouse.datasource.gallery.GalleryImagePagingSource
import com.lighthouse.domain.model.GalleryImage
import com.lighthouse.domain.repository.GalleryImageRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GalleryImageRepositoryImpl @Inject constructor(
    private val localSource: GalleryImageLocalSource
) : GalleryImageRepository {

    override fun getImages(pageSize: Int): Flow<PagingData<GalleryImage>> {
        return Pager(
            config = PagingConfig(pageSize = pageSize, enablePlaceholders = false),
            pagingSourceFactory = {
                GalleryImagePagingSource(localSource, 0, pageSize)
            }
        ).flow
    }
}
