package com.lighthouse.repository

import com.lighthouse.datasource.brand.BrandLocalDataSource
import com.lighthouse.datasource.brand.BrandRemoteDataSource
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
        brandName: String,
        x: String,
        y: String,
        radius: String,
        size: Int
    ): Result<List<BrandPlaceInfo>> {
        brandLocalSource.getBrands(x, y, brandName).onSuccess { brandEntities ->
            return Result.success(brandEntities.toDomain())
        }
        return getRemoteSourceData(brandName, x, y, radius, size)
    }

    private suspend fun getRemoteSourceData(
        brandName: String,
        x: String,
        y: String,
        radius: String,
        size: Int
    ): Result<List<BrandPlaceInfo>> {
        val result = brandRemoteSource.getBrandPlaceInfo(brandName, x, y, radius, size).mapCatching { it.toDomain() }
        val exception = result.exceptionOrNull()

        return if (exception is CustomErrorData) {
            Result.failure(exception.toDomain())
        } else {
            result
        }
    }
}
