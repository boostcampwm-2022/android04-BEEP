package com.lighthouse.repository

import com.lighthouse.beep.model.brand.BrandPlaceInfo
import com.lighthouse.beep.model.location.Dms
import com.lighthouse.datasource.brand.BrandLocalDataSource
import com.lighthouse.domain.repository.BrandRepository
import com.lighthouse.repository.brand.BrandRemoteRepository
import javax.inject.Inject

class BrandRepositoryImpl @Inject constructor(
    private val brandRemoteRepository: BrandRemoteRepository,
    private val brandLocalSource: BrandLocalDataSource
) : BrandRepository {

    override suspend fun getBrandPlaceInfo(
        brandName: String,
        x: Dms,
        y: Dms,
        size: Int
    ): Result<List<BrandPlaceInfo>> = Result.success(listOf())

//    override suspend fun getBrandPlaceInfo(
//        brandName: String,
//        x: Dms,
//        y: Dms,
//        size: Int
//    ): Result<List<BrandPlaceInfo>> = brandLocalSource.getBrands(x, y, brandName).mapCatching { it.toDomain() }
//        .recoverCatching {
//            getRemoteSourceData(brandName, x, y, size).getOrDefault(emptyList())
//            brandLocalSource.getBrands(x, y, brandName).mapCatching { it.toDomain() }.getOrDefault(emptyList())
//        }

    private suspend fun getRemoteSourceData(
        brandName: String,
        x: Dms,
        y: Dms,
        size: Int
    ): Result<List<BrandPlaceInfo>> {
        val result = brandRemoteRepository.getBrandPlaceInfo(brandName, x, y, size)
        val list = result.getOrNull()
        if (list != null) {
            brandLocalSource.insertBrands(list, x, y, brandName)
        }
        return result
    }

    override suspend fun removeExpirationBrands() {
        brandLocalSource.removeExpirationBrands()
    }
}
