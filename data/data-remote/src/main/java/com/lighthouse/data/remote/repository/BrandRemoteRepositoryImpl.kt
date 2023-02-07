package com.lighthouse.data.remote.repository

import com.lighthouse.beep.model.brand.BrandPlaceInfo
import com.lighthouse.beep.model.error.BeepError
import com.lighthouse.beep.model.location.Dms
import com.lighthouse.common.utils.geography.LocationConverter
import com.lighthouse.data.remote.api.KakaoApiService
import com.lighthouse.data.remote.mapper.toDomain
import com.lighthouse.repository.brand.BrandRemoteRepository
import java.net.UnknownHostException
import javax.inject.Inject

internal class BrandRemoteRepositoryImpl @Inject constructor(
    private val kakaoApiService: KakaoApiService
) : BrandRemoteRepository {

    override suspend fun getBrandPlaceInfo(
        brandName: String,
        x: Dms,
        y: Dms,
        size: Int
    ): Result<List<BrandPlaceInfo>> {
        val vertex = LocationConverter.getVertex(x, y)

        return try {
            val list = kakaoApiService.getAllBrandPlaceInfo(
                brandName,
                vertex,
                size
            ).documents.toDomain(brandName)
            Result.success(list)
        } catch (unknownHostException: UnknownHostException) {
            Result.failure(BeepError.NetworkFailure)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
