package com.lighthouse.repository

import com.lighthouse.datasource.brand.BrandLocalDataSource
import com.lighthouse.datasource.brand.BrandRemoteDataSource
import com.lighthouse.domain.Dms
import com.lighthouse.domain.model.BrandPlaceInfo
import com.lighthouse.domain.repository.BrandRepository
import com.lighthouse.mapper.toDomain
import com.lighthouse.model.CustomErrorData
import javax.inject.Inject

class BrandRepositoryImpl @Inject constructor(
    private val brandRemoteSource: BrandRemoteDataSource,
    private val brandLocalSource: BrandLocalDataSource
) : BrandRepository {

    override suspend fun getBrandPlaceInfo(
        brandNames: List<String>,
        x: Dms,
        y: Dms,
        size: Int
    ): Result<List<BrandPlaceInfo>> {
        brandLocalSource.getBrands(x, y).onSuccess { brandWithSections ->
            return Result.success(brandWithSections.toDomain())
        }
        return getRemoteSourceData(brandNames, x, y, size)
    }

    private suspend fun getRemoteSourceData(
        brandNames: List<String>,
        x: Dms,
        y: Dms,
        size: Int
    ): Result<List<BrandPlaceInfo>> {
        val result = brandRemoteSource.getBrandPlaceInfo(brandNames, x, y, size).mapCatching { it.toDomain() }
        val exception = result.exceptionOrNull()

        return if (exception is CustomErrorData) {
            Result.failure(exception.toDomain())
        } else {
            result.onSuccess { brandLocalSource.insertBrands(it, x, y) }
            result
        }
    }
}
