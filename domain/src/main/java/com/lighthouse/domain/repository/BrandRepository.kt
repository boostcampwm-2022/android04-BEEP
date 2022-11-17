package com.lighthouse.domain.repository

import com.lighthouse.domain.model.BrandPlaceInfo

interface BrandRepository {
    suspend fun getBrandPlaceInfo(brandName: String, x: String, y: String, radius: String, size: Int): Result<List<BrandPlaceInfo>>
}
