package com.lighthouse.datasource.brand

import com.lighthouse.database.dao.BrandWithSectionDao
import com.lighthouse.database.entity.BrandWithSections
import com.lighthouse.database.entity.SectionEntity
import com.lighthouse.domain.model.BrandPlaceInfo
import com.lighthouse.domain.model.CustomError
import com.lighthouse.util.LocationConverter
import javax.inject.Inject

class BrandLocalDataSourceImpl @Inject constructor(
    private val brandWithSectionDao: BrandWithSectionDao
) : BrandLocalDataSource {

    override suspend fun getBrands(x: String, y: String): Result<List<BrandWithSections>> {
        val xToDMS = LocationConverter.toDMS(x)
        val yToDMS = LocationConverter.toDMS(y)

        val sectionId = brandWithSectionDao.getSectionId(minX = xToDMS, minY = yToDMS)

        return if (sectionId == null) {
            Result.failure(CustomError.EmptyResults)
        } else {
            val brands = brandWithSectionDao.getBrands(sectionId)
            Result.success(brands)
        }
    }

    override suspend fun insertBrands(brandPlaceInfos: List<BrandPlaceInfo>, x: String, y: String) {
        if (brandPlaceInfos.isEmpty()) return

        val xToDMS = LocationConverter.toDMS(x)
        val yToDMS = LocationConverter.toDMS(y)
        brandWithSectionDao.insertSectionWithBrands(
            SectionEntity(minX = xToDMS, minY = yToDMS),
            brandPlaceInfos
        )
    }
}
