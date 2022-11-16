package com.lighthouse.datasource.brand

import com.lighthouse.model.BrandPlaceInfoDataContainer

interface BrandRemoteDataSource {
    suspend fun getBrandPlaceInfo(brandName: String, x: String, y: String, radius: String, size: Int): Result<BrandPlaceInfoDataContainer>
}
