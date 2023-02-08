package com.lighthouse.data.database.mapper.brand

import com.lighthouse.beep.model.brand.BrandPlaceInfo
import com.lighthouse.common.utils.geography.LocationConverter
import com.lighthouse.data.database.entity.DBBrandLocationEntity

internal fun List<BrandPlaceInfo>.toEntity(): List<DBBrandLocationEntity> {
    return map { brandPlaceInfo ->
        val x = LocationConverter.toMinDms(brandPlaceInfo.x.toDouble())
        val y = LocationConverter.toMinDms(brandPlaceInfo.y.toDouble())
        val sectionId = combineSectionId(x, y, brandPlaceInfo.brand)

        DBBrandLocationEntity(
            sectionId = sectionId,
            addressName = brandPlaceInfo.addressName,
            placeName = brandPlaceInfo.placeName,
            categoryName = brandPlaceInfo.categoryName,
            placeUrl = brandPlaceInfo.placeUrl,
            brand = brandPlaceInfo.brand,
            x = brandPlaceInfo.x,
            y = brandPlaceInfo.y
        )
    }
}
