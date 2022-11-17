package com.lighthouse.datasource.brand

import com.lighthouse.model.BrandPlaceInfoDataContainer
import com.lighthouse.model.CustomErrorData
import com.lighthouse.network.NetworkApiService
import com.lighthouse.util.LocationConverter
import java.net.UnknownHostException
import javax.inject.Inject

class BrandRemoteDataSourceImpl @Inject constructor(
    private val networkApiService: NetworkApiService
) : BrandRemoteDataSource {

    override suspend fun getBrandPlaceInfo(
        brandName: String,
        x: String,
        y: String,
        size: Int
    ): Result<BrandPlaceInfoDataContainer> {
        val vertex = LocationConverter.getVertex(x, y)

        val result = runCatching { networkApiService.getAllBrandPlaceInfo(brandName, vertex, size) }

        return when (val exception = result.exceptionOrNull()) {
            null -> result
            is UnknownHostException -> Result.failure(CustomErrorData.NetworkFailure)
            else -> Result.failure(exception)
        }
    }
}
