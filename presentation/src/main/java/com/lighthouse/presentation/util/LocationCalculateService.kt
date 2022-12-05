package com.lighthouse.presentation.util

import com.lighthouse.domain.LocationConverter
import com.lighthouse.presentation.model.BrandPlaceInfoUiModel

object LocationCalculateService {

    fun diffLocation(
        brandLocation: BrandPlaceInfoUiModel,
        x: Double,
        y: Double
    ) = LocationConverter.locationDistance(
        brandLocation.x.toDouble(),
        brandLocation.y.toDouble(),
        x,
        y
    )
}
