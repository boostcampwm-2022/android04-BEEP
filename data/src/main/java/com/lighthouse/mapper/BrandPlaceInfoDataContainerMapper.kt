package com.lighthouse.mapper

import com.lighthouse.domain.model.BrandPlaceInfo
import com.lighthouse.model.BrandPlaceInfoDataContainer

internal fun List<BrandPlaceInfoDataContainer.BrandPlaceInfoData>.toDomain(brandName: String): List<BrandPlaceInfo> {
    return this.map {
        BrandPlaceInfo(
            addressName = it.addressName,
            placeName = it.placeName,
            placeUrl = it.placeUrl,
            brand = brandName,
            x = it.x,
            y = it.y
        )
    }
}
