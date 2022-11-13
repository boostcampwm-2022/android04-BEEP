package com.lighthouse.domain.usecase

import com.lighthouse.domain.model.BrandPlaceInfo
import com.lighthouse.domain.model.CustomError
import com.lighthouse.domain.repository.BrandRepository
import javax.inject.Inject

class GetBrandPlaceInfosUseCase @Inject constructor(
    private val brandRepository: BrandRepository
) {

    suspend operator fun invoke(
        brandName: String,
        x: String,
        y: String,
        rect: String,
        size: Int
    ): Result<List<BrandPlaceInfo>> {
        val brandPlaceInfos = brandRepository.getBrandPlaceInfo(brandName, x, y, rect, size).getOrThrow()

        return if (brandPlaceInfos.isNotEmpty()) {
            Result.success(brandPlaceInfos)
        } else {
            Result.failure(CustomError.NotFoundBrandPlaceInfos)
        }
    }
}
