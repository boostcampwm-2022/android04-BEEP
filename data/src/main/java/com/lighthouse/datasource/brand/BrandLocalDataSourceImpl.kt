package com.lighthouse.datasource.brand

import com.lighthouse.database.dao.BrandDao
import com.lighthouse.database.entity.BrandEntity
import com.lighthouse.domain.model.BrandPlaceInfo
import com.lighthouse.model.CustomErrorData
import com.lighthouse.util.LocationConverter
import javax.inject.Inject

class BrandLocalDataSourceImpl @Inject constructor(
    private val brandDao: BrandDao
) : BrandLocalDataSource {

    override suspend fun getBrands(x: String, y: String, brandName: String): Result<List<BrandEntity>> {
        val xToDMS = LocationConverter.toDMS(x)
        val yToDMS = LocationConverter.toDMS(y)

        val brands = brandDao.getBrands(minX = xToDMS, minY = yToDMS)

        return if (brands.isEmpty()) {
            Result.failure(CustomErrorData.NotFoundBrandPlaceInfos)
        } else {
            Result.success(brands)
        }
    }

    override suspend fun insertBrands(brandPlaceInfos: List<BrandPlaceInfo>) = Unit
}
