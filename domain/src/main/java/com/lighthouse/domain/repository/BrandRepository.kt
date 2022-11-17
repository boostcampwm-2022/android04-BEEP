package com.lighthouse.domain.repository

import com.lighthouse.domain.model.BrandPlaceInfo

interface BrandRepository {
    suspend fun getBrandPlaceInfo(brandNames: List<String>, x: String, y: String, size: Int): Result<List<BrandPlaceInfo>>
}
