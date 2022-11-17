package com.lighthouse.mapper

import com.lighthouse.domain.model.BrandPlaceInfo
import com.lighthouse.model.BrandPlaceInfoDataContainer

internal fun BrandPlaceInfoDataContainer.toDomain(): List<BrandPlaceInfo> {
    val brandName = this.meta.sameName.keyword
    return this.documents.map {
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
