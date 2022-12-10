package com.lighthouse.domain.usecase

import com.lighthouse.domain.LocationConverter
import com.lighthouse.domain.model.BrandPlaceInfo
import com.lighthouse.domain.repository.BrandRepository
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
        val cardinalLocations = LocationConverter.getCardinalDirections(x, y)

        return runCatching {
            cardinalLocations.flatMap { location ->
                brandNames.flatMap { brandName ->
                    brandRepository.getBrandPlaceInfo(brandName, location.x, location.y, size).getOrThrow()
                }
            }
        }
    }
}
