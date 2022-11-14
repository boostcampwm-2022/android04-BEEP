package com.lighthouse.datasource

import com.lighthouse.model.BrandPlaceInfoDataContainer
import com.lighthouse.model.CustomErrorData
import com.lighthouse.network.NetworkApiService
import java.net.UnknownHostException
import javax.inject.Inject

class BrandRemoteSourceImpl @Inject constructor(
    private val networkApiService: NetworkApiService
) : BrandRemoteSource {

    override suspend fun getBrandPlaceInfo(
        brandName: String,
        x: String,
        y: String,
        radius: String,
        size: Int
    ): Result<BrandPlaceInfoDataContainer> {
        val result = runCatching { networkApiService.getAllBrandPlaceInfo(brandName, x, y, radius, size) }

        return when (val exception = result.exceptionOrNull()) {
            null -> result
            is UnknownHostException -> Result.failure(CustomErrorData.NetworkFailure)
            else -> Result.failure(exception)
        }
    }
}
