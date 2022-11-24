package com.lighthouse.mapper

import com.lighthouse.database.entity.BrandLocationEntity
import com.lighthouse.datasource.brand.BrandLocalDataSourceImpl
import com.lighthouse.domain.LocationConverter
import com.lighthouse.domain.model.BrandPlaceInfo

fun List<BrandLocationEntity>.toDomain(): List<BrandPlaceInfo> = this.map { brandLocationEntity ->
    BrandPlaceInfo(
        addressName = brandLocationEntity.addressName,
        placeName = brandLocationEntity.placeName,
        placeUrl = brandLocationEntity.placeUrl,
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
            placeUrl = brandPlaceInfo.placeUrl,
            brand = brandPlaceInfo.brand,
            x = brandPlaceInfo.x,
            y = brandPlaceInfo.y
        )
    }
}
