package com.lighthouse.presentation.mapper

import com.lighthouse.domain.model.BrandPlaceInfo
import com.lighthouse.presentation.model.BrandPlaceInfoUiModel

fun BrandPlaceInfo.toPresentation(): BrandPlaceInfoUiModel = BrandPlaceInfoUiModel(
    addressName = this.addressName,
    placeName = this.placeName,
    placeUrl = this.placeUrl,
    brand = this.brand,
    x = this.x,
    y = this.y
)
