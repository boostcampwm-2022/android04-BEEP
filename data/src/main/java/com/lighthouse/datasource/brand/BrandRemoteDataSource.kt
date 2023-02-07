package com.lighthouse.datasource.brand

import com.lighthouse.beep.model.location.Dms
import com.lighthouse.model.BrandPlaceInfoDataContainer

interface BrandRemoteDataSource {

    suspend fun getBrandPlaceInfo(
        brandName: String,
        x: Dms,
        y: Dms,
        size: Int
    ): Result<List<BrandPlaceInfoDataContainer.BrandPlaceInfoData>>
}
