package com.lighthouse.domain.repository

import com.lighthouse.beep.model.brand.BrandPlaceInfo
import com.lighthouse.beep.model.location.Dms

interface BrandRepository {

    suspend fun getBrandPlaceInfo(
        brandName: String,
        x: Dms,
        y: Dms,
        size: Int
    ): Result<List<BrandPlaceInfo>>

    suspend fun removeExpirationBrands(): Result<Unit>
}
