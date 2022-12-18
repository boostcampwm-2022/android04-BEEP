package com.lighthouse.domain.usecase.edit

import com.lighthouse.domain.repository.GifticonRepository
import javax.inject.Inject

class HasGifticonBrandUseCase @Inject constructor(
    private val gifticonRepository: GifticonRepository
) {
    suspend operator fun invoke(brand: String): Boolean {
        return gifticonRepository.hasGifticonBrand(brand)
    }
}
