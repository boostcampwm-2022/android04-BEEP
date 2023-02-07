package com.lighthouse.domain.usecase

import com.lighthouse.beep.model.brand.BrandPlaceInfo
import com.lighthouse.common.utils.geography.LocationConverter
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
