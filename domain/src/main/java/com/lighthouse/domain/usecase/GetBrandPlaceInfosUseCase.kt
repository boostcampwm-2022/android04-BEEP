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
        size: Int
    ): Result<List<BrandPlaceInfo>> {
        val brandSearchResults = brandRepository.getBrandPlaceInfo(brandNames, x, y, size).getOrThrow()

        return if (brandSearchResults.isNotEmpty()) {
            Result.success(brandSearchResults)
        } else {
            Result.failure(CustomError.EmptyResults)
        }
    }
}
