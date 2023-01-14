package com.lighthouse.domain.usecase

import com.lighthouse.domain.repository.BrandRepository
import javax.inject.Inject

class RemoveExpirationBrandUseCase @Inject constructor(
    private val brandRepository: BrandRepository
) {

    suspend operator fun invoke() {
        brandRepository.removeExpirationBrands()
    }
}
