package com.lighthouse.datasource.brand

import com.lighthouse.model.BrandPlaceInfoDataContainer

interface BrandRemoteDataSource {
    suspend fun getBrandPlaceInfo(brandNames: List<String>, x: String, y: String, size: Int): Result<List<BrandPlaceInfoDataContainer>>
}
