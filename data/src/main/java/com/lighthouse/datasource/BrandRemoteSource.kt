package com.lighthouse.datasource

import com.lighthouse.model.BrandPlaceInfoDataContainer

interface BrandRemoteSource {
    suspend fun getBrandPlaceInfo(brandName: String, x: String, y: String, rect: String, size: Int): Result<BrandPlaceInfoDataContainer>
}
