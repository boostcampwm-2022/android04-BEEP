package com.lighthouse.data.remote.api

import com.lighthouse.data.remote.model.BrandPlaceInfoDataContainer
import retrofit2.http.GET
import retrofit2.http.Query

internal interface KakaoApiService {

    @GET("v2/local/search/keyword.json")
    suspend fun getAllBrandPlaceInfo(
        @Query("query") query: String,
        @Query("rect") rect: String,
        @Query("size") size: Int,
        @Query("category_group_code") groupCode: String = CATEGORY_GROUP_CODE
    ): BrandPlaceInfoDataContainer

    companion object {
        private const val CATEGORY_GROUP_CODE = "MT1,CS2,CT1,AD5,FD6,CE7"
    }
}
