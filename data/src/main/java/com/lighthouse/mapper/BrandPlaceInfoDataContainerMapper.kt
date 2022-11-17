package com.lighthouse.mapper

import com.lighthouse.domain.model.BrandPlaceInfo
import com.lighthouse.model.BrandPlaceInfoDataContainer

internal fun List<BrandPlaceInfoDataContainer>.toDomain(): List<BrandPlaceInfo> {
    return this.map { brandPlaceInfoDataContainer ->
        val brandName = brandPlaceInfoDataContainer.meta.sameName.keyword
        brandPlaceInfoDataContainer.documents.map {
            BrandPlaceInfo(
                addressName = it.addressName,
                placeName = it.placeName,
                placeUrl = it.placeUrl,
                brand = brandName,
                x = it.x,
                y = it.y
            )
        }
    }.flatten()
}
