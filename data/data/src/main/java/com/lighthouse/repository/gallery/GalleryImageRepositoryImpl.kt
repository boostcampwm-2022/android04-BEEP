package com.lighthouse.repository.gallery

import androidx.paging.PagingData
import com.lighthouse.beep.model.gallery.GalleryImage
import com.lighthouse.domain.repository.GalleryImageRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GalleryImageRepositoryImpl @Inject constructor(
    private val galleryImageContentRepository: GalleryImageContentRepository
) : GalleryImageRepository {

    override fun getImages(pageSize: Int): Flow<PagingData<GalleryImage>> {
        return galleryImageContentRepository.getImages(pageSize)
    }
}
