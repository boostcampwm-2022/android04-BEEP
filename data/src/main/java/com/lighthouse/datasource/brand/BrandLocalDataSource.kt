package com.lighthouse.datasource.brand

import com.lighthouse.database.entity.BrandEntity
import com.lighthouse.domain.model.BrandPlaceInfo

interface BrandLocalDataSource {

    suspend fun getBrands(x: String, y: String, brandName: String): Result<List<BrandEntity>>
    suspend fun insertBrands(brandPlaceInfos: List<BrandPlaceInfo>)
}
