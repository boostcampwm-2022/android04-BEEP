package com.lighthouse.network

import com.lighthouse.model.BrandPlaceInfoDataContainer
import retrofit2.http.GET
import retrofit2.http.Query

interface NetworkApiService {

    @GET("v2/local/search/keyword.json")
    suspend fun getAllBrandPlaceInfo(
        @Query("query") query: String,
        @Query("x") x: String,
        @Query("y") y: String,
        @Query("rect") rect: String,
        @Query("radius") radius: Int
    ): BrandPlaceInfoDataContainer
}
