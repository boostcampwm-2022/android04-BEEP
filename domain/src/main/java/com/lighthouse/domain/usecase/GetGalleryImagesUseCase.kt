package com.lighthouse.domain.usecase

import androidx.paging.PagingData
import com.lighthouse.beep.model.gallery.GalleryImage
import com.lighthouse.domain.repository.gallery.GalleryImageRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetGalleryImagesUseCase @Inject constructor(
    private val galleryImageRepository: GalleryImageRepository
) {

    operator fun invoke(): Flow<PagingData<GalleryImage>> {
        return galleryImageRepository.getImages(20)
    }
}
