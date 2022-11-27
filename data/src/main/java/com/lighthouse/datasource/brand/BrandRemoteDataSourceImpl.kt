package com.lighthouse.datasource.brand

import com.lighthouse.domain.Dms
import com.lighthouse.domain.LocationConverter
import com.lighthouse.model.BrandPlaceInfoDataContainer
import com.lighthouse.model.BeepErrorData
import com.lighthouse.network.NetworkApiService
import java.net.UnknownHostException
import javax.inject.Inject

class BrandRemoteDataSourceImpl @Inject constructor(
    private val networkApiService: NetworkApiService
) : BrandRemoteDataSource {

    override suspend fun getBrandPlaceInfo(
        brandName: String,
        x: Dms,
        y: Dms,
        size: Int
    ): Result<List<BrandPlaceInfoDataContainer.BrandPlaceInfoData>> {
        val vertex = LocationConverter.getVertex(x, y)

        val result = runCatching { networkApiService.getAllBrandPlaceInfo(brandName, vertex, size).documents }
        return when (val exception = result.exceptionOrNull()) {
            null -> result
            is UnknownHostException -> Result.failure(BeepErrorData.NetworkFailure)
            else -> Result.failure(exception)
        }
    }
}
