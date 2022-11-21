package com.lighthouse.domain.usecase

import com.lighthouse.domain.LocationConverter
import com.lighthouse.domain.model.BrandPlaceInfo
import com.lighthouse.domain.model.CustomError
import com.lighthouse.domain.repository.BrandRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetBrandPlaceInfosUseCase @Inject constructor(
    private val brandRepository: BrandRepository
) {

    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO

    suspend operator fun invoke(
        brandNames: List<String>,
        x: Double,
        y: Double,
        size: Int
    ): Result<List<BrandPlaceInfo>> {
        val cardinalLocations = LocationConverter.getCardinalDirections(x, y)

        val brandSearchResults = withContext(ioDispatcher) {
            cardinalLocations.flatMap { location ->
                brandRepository.getBrandPlaceInfo(brandNames, location.x, location.y, size).getOrThrow()
            }
        }

        return if (brandSearchResults.isNotEmpty()) {
            Result.success(brandSearchResults)
        } else {
            Result.failure(CustomError.EmptyResults)
        }
    }
}
