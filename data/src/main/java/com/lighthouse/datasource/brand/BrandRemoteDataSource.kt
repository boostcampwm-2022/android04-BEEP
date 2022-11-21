package com.lighthouse.datasource.brand

import com.lighthouse.domain.Dms
import com.lighthouse.model.BrandPlaceInfoDataContainer

interface BrandRemoteDataSource {

    suspend fun getBrandPlaceInfo(
        brandNames: List<String>,
        x: Dms,
        y: Dms,
        size: Int
    ): Result<List<BrandPlaceInfoDataContainer>>
}
