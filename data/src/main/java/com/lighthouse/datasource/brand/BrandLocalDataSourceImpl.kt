package com.lighthouse.datasource.brand

import com.lighthouse.database.dao.BrandWithSectionDao
import com.lighthouse.database.entity.BrandWithSections
import com.lighthouse.database.entity.SectionEntity
import com.lighthouse.domain.Dms
import com.lighthouse.domain.model.BrandPlaceInfo
import com.lighthouse.domain.model.CustomError
import javax.inject.Inject

class BrandLocalDataSourceImpl @Inject constructor(
    private val brandWithSectionDao: BrandWithSectionDao
) : BrandLocalDataSource {

    override suspend fun getBrands(
        x: Dms,
        y: Dms
    ): Result<List<BrandWithSections>> {
        val sectionId = brandWithSectionDao.getSectionId(x.dmsToString(), y.dmsToString())

        return if (sectionId == null) {
            Result.failure(CustomError.EmptyResults)
        } else {
            val brands = brandWithSectionDao.getBrands(sectionId)
            Result.success(brands)
        }
    }

    override suspend fun insertBrands(
        brandPlaceInfos: List<BrandPlaceInfo>,
        x: Dms,
        y: Dms
    ) {
        brandWithSectionDao.insertSectionWithBrands(
            SectionEntity(minX = x.dmsToString(), minY = y.dmsToString()),
            brandPlaceInfos
        )
    }
}
