package com.lighthouse.mapper

import com.lighthouse.database.entity.BrandWithSections
import com.lighthouse.domain.model.BrandPlaceInfo

fun List<BrandWithSections>.toDomain(): List<BrandPlaceInfo> = this.map { brandWithSection ->
    return brandWithSection.brands.map {
        BrandPlaceInfo(
            addressName = it.addressName,
            placeName = it.placeName,
            placeUrl = it.placeUrl,
            brand = it.brand,
            x = it.x,
            y = it.y
        )
    }
}
