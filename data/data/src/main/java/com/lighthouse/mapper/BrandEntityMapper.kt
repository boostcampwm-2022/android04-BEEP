package com.lighthouse.mapper

import com.lighthouse.beep.model.brand.BrandPlaceInfo
import com.lighthouse.common.utils.geography.LocationConverter
import com.lighthouse.database.entity.BrandLocationEntity
import com.lighthouse.datasource.brand.BrandLocalDataSourceImpl

fun List<BrandLocationEntity>.toDomain(): List<BrandPlaceInfo> = this.map { brandLocationEntity ->
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

fun List<BrandPlaceInfo>.toEntity(): List<BrandLocationEntity> {
    return this.map { brandPlaceInfo ->
        val x = LocationConverter.toMinDms(brandPlaceInfo.x.toDouble())
        val y = LocationConverter.toMinDms(brandPlaceInfo.y.toDouble())
        val id = BrandLocalDataSourceImpl.combineSectionId(x.dmsToString(), y.dmsToString(), brandPlaceInfo.brand)

        BrandLocationEntity(
            sectionId = id,
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
