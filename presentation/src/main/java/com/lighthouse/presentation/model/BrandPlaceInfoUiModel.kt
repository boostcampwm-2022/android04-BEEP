package com.lighthouse.presentation.model

import java.io.Serializable

data class BrandPlaceInfoUiModel(
    val addressName: String,
    val placeName: String,
    val categoryName: String,
    val placeUrl: String,
    val brand: String,
    val x: String,
    val y: String
) : Serializable
