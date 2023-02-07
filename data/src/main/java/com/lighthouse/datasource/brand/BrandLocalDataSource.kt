package com.lighthouse.datasource.brand

import com.lighthouse.beep.model.brand.BrandPlaceInfo
import com.lighthouse.beep.model.location.Dms
import com.lighthouse.database.entity.BrandLocationEntity

interface BrandLocalDataSource {

    suspend fun getBrands(x: Dms, y: Dms, brandName: String): Result<List<BrandLocationEntity>>

    suspend fun insertBrands(
        brandPlaceInfos: List<BrandPlaceInfo>,
        x: Dms,
        y: Dms,
        brandName: String
    )

    suspend fun isNearBrand(x: Dms, y: Dms, brandName: String): List<BrandLocationEntity>?

    suspend fun removeExpirationBrands()
}
