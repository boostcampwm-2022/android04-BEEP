package com.lighthouse.domain.repository

import com.lighthouse.domain.Dms
import com.lighthouse.domain.DmsLocation
import com.lighthouse.domain.model.BrandPlaceInfo

interface BrandRepository {
    suspend fun getBrandPlaceInfo(brandName: String, x: Dms, y: Dms, size: Int): Result<List<BrandPlaceInfo>>
    suspend fun getNearByBrands(brandName: String, cardinalLocations: List<DmsLocation>): Result<Boolean>
}
