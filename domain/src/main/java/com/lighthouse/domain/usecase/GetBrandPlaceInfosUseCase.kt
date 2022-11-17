package com.lighthouse.domain.usecase

import com.lighthouse.domain.model.BrandPlaceInfo
import com.lighthouse.domain.model.CustomError
import com.lighthouse.domain.repository.BrandRepository
import javax.inject.Inject

class GetBrandPlaceInfosUseCase @Inject constructor(
    private val brandRepository: BrandRepository
) {

    suspend operator fun invoke(
        brandNames: List<String>,
        x: String,
        y: String,
        radius: String,
        size: Int
    ): Result<List<BrandPlaceInfo>> {
        val brandPlaceInfos = mutableListOf<BrandPlaceInfo>()

        for (brandName in brandNames) {
            val brandSearchResults = brandRepository.getBrandPlaceInfo(brandName, x, y, radius, size).getOrThrow()
            if (brandSearchResults.isNotEmpty()) brandPlaceInfos.addAll(brandSearchResults)
        }

        return if (brandPlaceInfos.isNotEmpty()) {
            Result.success(brandPlaceInfos)
        } else {
            Result.failure(CustomError.NotFoundBrandPlaceInfos)
        }
    }
}
