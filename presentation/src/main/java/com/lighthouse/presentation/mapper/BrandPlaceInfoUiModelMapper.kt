package com.lighthouse.presentation.mapper

import com.lighthouse.beep.model.brand.BrandPlaceInfo
import com.lighthouse.presentation.model.BrandPlaceInfoUiModel

fun List<BrandPlaceInfo>.toPresentation(): List<BrandPlaceInfoUiModel> = map {
    BrandPlaceInfoUiModel(
        addressName = it.addressName,
        placeName = it.placeName,
        categoryName = it.categoryName,
        placeUrl = it.placeUrl,
        brand = it.brand,
        x = it.x,
        y = it.y
    )
}
