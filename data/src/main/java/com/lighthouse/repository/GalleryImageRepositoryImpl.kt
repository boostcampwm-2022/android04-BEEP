package com.lighthouse.repository

import com.lighthouse.datasource.GalleryImageLocalSource
import com.lighthouse.domain.model.GalleryImage
import com.lighthouse.domain.repository.GalleryImageRepository
import javax.inject.Inject

class GalleryImageRepositoryImpl @Inject constructor(
    private val localSource: GalleryImageLocalSource
) : GalleryImageRepository {

    override suspend fun getImages(): List<GalleryImage> {
        return localSource.getImages()
    }
}
