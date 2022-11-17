package com.lighthouse.network

import com.lighthouse.model.BrandPlaceInfoDataContainer
import retrofit2.http.GET
import retrofit2.http.Query

interface NetworkApiService {

    @GET("v2/local/search/keyword.json")
    suspend fun getAllBrandPlaceInfo(
        @Query("query") query: String,
        @Query("rect") rect: String,
        @Query("size") size: Int
    ): BrandPlaceInfoDataContainer
}
