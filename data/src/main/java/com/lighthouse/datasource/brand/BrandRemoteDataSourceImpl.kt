package com.lighthouse.datasource.brand

import com.lighthouse.domain.Dms
import com.lighthouse.domain.LocationConverter
import com.lighthouse.model.BrandPlaceInfoDataContainer
import com.lighthouse.model.CustomErrorData
import com.lighthouse.network.NetworkApiService
import java.net.UnknownHostException
import javax.inject.Inject

class BrandRemoteDataSourceImpl @Inject constructor(
    private val networkApiService: NetworkApiService
) : BrandRemoteDataSource {

    override suspend fun getBrandPlaceInfo(
        brandNames: List<String>,
        x: Dms,
        y: Dms,
        size: Int
    ): Result<List<BrandPlaceInfoDataContainer>> {
        val vertex = LocationConverter.getVertex(x, y)

        val result = runCatching {
            brandNames.map { brandName ->
                networkApiService.getAllBrandPlaceInfo(brandName, vertex, size)
            }
        }

        return when (val exception = result.exceptionOrNull()) {
            null -> result
            is UnknownHostException -> Result.failure(CustomErrorData.NetworkFailure)
            else -> Result.failure(exception)
        }
    }
}
