package com.lighthouse.repository.brand

import com.lighthouse.beep.model.brand.BrandPlaceInfo
import com.lighthouse.beep.model.location.Dms

interface BrandDatabaseRepository {
    suspend fun getBrands(
        x: Dms,
        y: Dms,
        brandName: String
    ): Result<List<BrandPlaceInfo>>

    suspend fun insertBrands(
        brandPlaceInfoList: List<BrandPlaceInfo>,
        x: Dms,
        y: Dms,
        brandName: String
    ): Result<Unit>

    suspend fun removeExpirationBrands(): Result<Unit>
}
