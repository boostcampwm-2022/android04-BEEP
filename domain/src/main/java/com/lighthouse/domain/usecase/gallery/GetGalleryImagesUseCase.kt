package com.lighthouse.domain.usecase.gallery

import androidx.paging.PagingData
import com.lighthouse.domain.model.GalleryImage
import com.lighthouse.domain.repository.GalleryImageRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetGalleryImagesUseCase @Inject constructor(
    private val galleryImageRepository: GalleryImageRepository
) {

    operator fun invoke(): Flow<PagingData<GalleryImage>> {
        return galleryImageRepository.getImages()
    }
}
