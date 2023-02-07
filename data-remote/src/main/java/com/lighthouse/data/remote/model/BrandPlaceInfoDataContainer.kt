package com.lighthouse.data.remote.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class BrandPlaceInfoDataContainer(
    val documents: List<BrandPlaceInfoData>,
    val meta: Meta
) {
    @JsonClass(generateAdapter = true)
    data class BrandPlaceInfoData(
        @field:Json(name = "address_name") val addressName: String,
        @field:Json(name = "category_group_code") val categoryGroupCode: String,
        @field:Json(name = "category_group_name") val categoryGroupName: String,
        @field:Json(name = "category_name") val categoryName: String,
        @field:Json(name = "distance") val distance: String,
        @field:Json(name = "id") val id: String,
        @field:Json(name = "phone") val phone: String,
        @field:Json(name = "place_name") val placeName: String,
        @field:Json(name = "place_url") val placeUrl: String,
        @field:Json(name = "road_address_name") val roadAddressName: String,
        @field:Json(name = "x") val x: String,
        @field:Json(name = "y") val y: String
    )

    @JsonClass(generateAdapter = true)
    data class Meta(
        @field:Json(name = "is_end") val isEnd: Boolean,
        @field:Json(name = "pageable_count") val pageableCount: Int,
        @field:Json(name = "same_name") val sameName: SameName,
        @field:Json(name = "total_count") val totalCount: Int
    )

    @JsonClass(generateAdapter = true)
    data class SameName(
        @field:Json(name = "keyword") val keyword: String,
        @field:Json(name = "region") val region: List<Any>,
        @field:Json(name = "selected_region") val selectedRegion: String
    )
}
