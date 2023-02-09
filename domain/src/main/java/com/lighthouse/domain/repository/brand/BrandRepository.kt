package com.lighthouse.domain.repository.brand

import com.lighthouse.beep.model.brand.BrandPlaceInfo
import com.lighthouse.beep.model.location.Dms

interface BrandRepository {

    suspend fun getAroundBrandPlaceInfo(
        brandNames: List<String>,
        x: Double,
        y: Double,
        size: Int
    ): Result<List<BrandPlaceInfo>>

    suspend fun getBrandPlaceInfo(
        brandName: String,
        x: Dms,
        y: Dms,
        size: Int
    ): Result<List<BrandPlaceInfo>>

    suspend fun removeExpirationBrands(): Result<Unit>
}
