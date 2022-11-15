package com.lighthouse.datasource

import com.lighthouse.model.BrandPlaceInfoDataContainer

interface BrandRemoteSource {
    suspend fun getBrandPlaceInfo(brandName: String, x: String, y: String, radius: String, size: Int): Result<BrandPlaceInfoDataContainer>
}
