package com.lighthouse.domain.usecase

import com.lighthouse.domain.LocationConverter
import com.lighthouse.domain.repository.BrandRepository
import javax.inject.Inject

class GetNearBrandsUseCase @Inject constructor(
    private val brandRepository: BrandRepository
) {
    suspend operator fun invoke(
        brandNames: List<String>,
        x: Double,
        y: Double
    ): List<String> {
        return brandNames.filter { brandName ->
            brandRepository.isNearBrand(brandName, LocationConverter.getCardinalDirections(x, y)).getOrDefault(false)
        }
    }
}
