package com.lighthouse.data.content.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.lighthouse.beep.model.gallery.GalleryImage
import com.lighthouse.data.content.datasource.GalleryImageDataSource
import com.lighthouse.data.content.datasource.GalleryImagePagingSource
import com.lighthouse.repository.gallery.GalleryImageContentRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class GalleryImageContentRepositoryImpl @Inject constructor(
    private val dataSource: GalleryImageDataSource
) : GalleryImageContentRepository {

    override fun getImages(pageSize: Int): Flow<PagingData<GalleryImage>> {
        return Pager(
            config = PagingConfig(pageSize = pageSize, enablePlaceholders = false),
            pagingSourceFactory = {
                GalleryImagePagingSource(dataSource, 0, pageSize)
            }
        ).flow
    }
}
