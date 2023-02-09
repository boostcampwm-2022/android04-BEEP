package com.lighthouse.domain.usecase

import com.lighthouse.beep.model.brand.BrandPlaceInfo
import com.lighthouse.domain.repository.brand.BrandRepository
import javax.inject.Inject

class GetBrandPlaceInfosUseCase @Inject constructor(
    private val brandRepository: BrandRepository
) {

    suspend operator fun invoke(
        brandNames: List<String>,
        x: Double,
        y: Double,
        size: Int
    ): Result<List<BrandPlaceInfo>> {
        return brandRepository.getAroundBrandPlaceInfo(brandNames, x, y, size)
    }
}
