package com.lighthouse.mapper

import com.lighthouse.database.entity.BrandEntity
import com.lighthouse.domain.model.BrandPlaceInfo

fun List<BrandEntity>.toDomain(): List<BrandPlaceInfo> = this.map {
    BrandPlaceInfo(
        addressName = it.addressName,
        placeName = it.placeName,
        placeUrl = it.placeUrl,
        brand = it.brand,
        x = it.x,
        y = it.y
    )
}
