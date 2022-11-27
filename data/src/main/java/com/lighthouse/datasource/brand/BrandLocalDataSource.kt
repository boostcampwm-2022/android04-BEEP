package com.lighthouse.datasource.brand

import com.lighthouse.database.entity.BrandLocationEntity
import com.lighthouse.domain.Dms
import com.lighthouse.domain.model.BrandPlaceInfo

interface BrandLocalDataSource {

    suspend fun getBrands(x: Dms, y: Dms, brandName: String): Result<List<BrandLocationEntity>>

    suspend fun insertBrands(brandPlaceInfos: List<BrandPlaceInfo>, x: Dms, y: Dms, brandName: String)

    suspend fun insertSection(x: Dms, y: Dms, brandName: String)

    suspend fun isNearBrand(x: Dms, y: Dms, brandName: String): Boolean
}
