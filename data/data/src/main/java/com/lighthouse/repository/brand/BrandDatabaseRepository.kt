package com.lighthouse.repository.brand

import com.lighthouse.beep.model.brand.BrandPlaceInfo
import com.lighthouse.beep.model.location.Dms
import com.lighthouse.beep.model.result.DbResult

interface BrandDatabaseRepository {
    suspend fun getBrands(
        x: Dms,
        y: Dms,
        brandName: String
    ): DbResult<List<BrandPlaceInfo>>

    suspend fun insertBrands(
        brandPlaceInfoList: List<BrandPlaceInfo>,
        x: Dms,
        y: Dms,
        brandName: String
    ): DbResult<Unit>

    suspend fun removeExpirationBrands(): DbResult<Unit>
}
