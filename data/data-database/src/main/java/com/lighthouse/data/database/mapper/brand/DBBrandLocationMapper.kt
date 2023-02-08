package com.lighthouse.data.database.mapper.brand

import com.lighthouse.beep.model.brand.BrandPlaceInfo
import com.lighthouse.data.database.entity.DBBrandLocationEntity

internal fun List<DBBrandLocationEntity>.toDomain(): List<BrandPlaceInfo> {
    return map { brandLocationEntity ->
        BrandPlaceInfo(
            addressName = brandLocationEntity.addressName,
            placeName = brandLocationEntity.placeName,
            placeUrl = brandLocationEntity.placeUrl,
            categoryName = brandLocationEntity.categoryName,
            brand = brandLocationEntity.brand,
            x = brandLocationEntity.x,
            y = brandLocationEntity.y
        )
    }
}
