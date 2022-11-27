package com.lighthouse.domain.usecase

import com.lighthouse.domain.LocationConverter
import com.lighthouse.domain.repository.BrandRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetNearBrandsUseCase @Inject constructor(
    private val brandRepository: BrandRepository
) {
    suspend operator fun invoke(
        brandNames: List<String>,
        x: Double,
        y: Double
    ): List<String> = withContext(Dispatchers.IO) {
        brandNames.filter { brandName ->
            brandRepository.isNearBrand(brandName, LocationConverter.getCardinalDirections(x, y)).getOrThrow()
        }
    }
}
