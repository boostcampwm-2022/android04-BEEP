package com.lighthouse.datasource.brand

import com.lighthouse.database.entity.BrandWithSections
import com.lighthouse.domain.model.BrandPlaceInfo

interface BrandLocalDataSource {

    suspend fun getBrands(x: String, y: String): Result<List<BrandWithSections>>

    suspend fun insertBrands(brandPlaceInfos: List<BrandPlaceInfo>, x: String, y: String)
}
