package com.lighthouse.domain.usecase

import com.lighthouse.domain.model.BrandPlaceInfo

class GetBrandPlaceInfosUseCase {

    operator fun invoke(brandName: String, x: Int, y: Int, rect: Int, size: Int): List<BrandPlaceInfo> {
        return emptyList()
    }
}
