package com.lighthouse.domain.usecase

import com.lighthouse.domain.model.GalleryImage
import com.lighthouse.domain.repository.GalleryImageRepository
import javax.inject.Inject

class GetGalleryImagesUseCase @Inject constructor(
    private val galleryImageRepository: GalleryImageRepository
) {

    suspend operator fun invoke(): List<GalleryImage> {
        return galleryImageRepository.getImages()
    }
}
