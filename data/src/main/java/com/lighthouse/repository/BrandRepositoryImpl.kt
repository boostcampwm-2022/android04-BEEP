package com.lighthouse.repository

import com.lighthouse.datasource.BrandRemoteSource
import com.lighthouse.domain.model.BrandPlaceInfo
import com.lighthouse.domain.repository.BrandRepository
import com.lighthouse.mapper.toDomain
import com.lighthouse.model.CustomErrorData
import javax.inject.Inject

class BrandRepositoryImpl @Inject constructor(
    private val remoteBrandSource: BrandRemoteSource
) : BrandRepository {

    override suspend fun getBrandPlaceInfo(
        brandName: String,
        x: String,
        y: String,
        radius: String,
        size: Int
    ): Result<List<BrandPlaceInfo>> {
        val result = remoteBrandSource.getBrandPlaceInfo(brandName, x, y, radius, size).mapCatching { it.toDomain() }
        val exception = result.exceptionOrNull()

        return if (exception is CustomErrorData) {
            Result.failure(exception.toDomain())
        } else {
            result
        }
    }
}
