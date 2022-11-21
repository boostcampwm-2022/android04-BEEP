package com.lighthouse.datasource.brand

import com.lighthouse.database.entity.BrandWithSections
import com.lighthouse.domain.Dms
import com.lighthouse.domain.model.BrandPlaceInfo

interface BrandLocalDataSource {

    suspend fun getBrands(x: Dms, y: Dms): Result<List<BrandWithSections>>

    suspend fun insertBrands(brandPlaceInfos: List<BrandPlaceInfo>, x: Dms, y: Dms)
}
