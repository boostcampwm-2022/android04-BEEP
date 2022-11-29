package com.lighthouse.repository

import com.lighthouse.datasource.brand.BrandLocalDataSource
import com.lighthouse.datasource.brand.BrandRemoteDataSource
import com.lighthouse.domain.Dms
import com.lighthouse.domain.DmsLocation
import com.lighthouse.domain.model.BrandPlaceInfo
import com.lighthouse.domain.repository.BrandRepository
import com.lighthouse.mapper.toDomain
import com.lighthouse.model.BeepErrorData
import javax.inject.Inject

class BrandRepositoryImpl @Inject constructor(
    private val brandRemoteSource: BrandRemoteDataSource,
    private val brandLocalSource: BrandLocalDataSource
) : BrandRepository {

    override suspend fun getBrandPlaceInfo(
        brandName: String,
        x: Dms,
        y: Dms,
        size: Int
    ): Result<List<BrandPlaceInfo>> = brandLocalSource.getBrands(x, y, brandName).mapCatching { it.toDomain() }
        .recoverCatching {
            getRemoteSourceData(brandName, x, y, size).getOrDefault(emptyList())
            brandLocalSource.getBrands(x, y, brandName).mapCatching { it.toDomain() }.getOrDefault(emptyList())
        }

    override suspend fun getNearByBrands(brandName: String, cardinalLocations: List<DmsLocation>): Result<Boolean> {
        cardinalLocations.forEach { location ->
            val isNearBrand = brandLocalSource.isNearBrand(location.x, location.y, brandName)
            if (isNearBrand == null) {
                val result = getRemoteSourceData(brandName, location.x, location.y, DEFAULT_SIZE).exceptionOrNull()
                if (result is BeepErrorData) {
                    return Result.failure(result.toDomain())
                }
            } else if (isNearBrand.isNotEmpty()) {
                return Result.success(true)
            }
        }
        return Result.success(false)
    }

    private suspend fun getRemoteSourceData(
        brandName: String,
        x: Dms,
        y: Dms,
        size: Int
    ): Result<List<BrandPlaceInfo>> {
        val result = brandRemoteSource.getBrandPlaceInfo(brandName, x, y, size).mapCatching { it.toDomain(brandName) }
        val exception = result.exceptionOrNull()

        return if (exception is BeepErrorData) {
            Result.failure(exception.toDomain())
        } else {
            result.onSuccess { brandLocalSource.insertBrands(it, x, y, brandName) }
        }
    }

    companion object {
        private const val DEFAULT_SIZE = 15
    }
}
