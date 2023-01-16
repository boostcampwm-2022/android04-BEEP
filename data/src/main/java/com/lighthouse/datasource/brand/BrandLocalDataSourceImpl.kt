package com.lighthouse.datasource.brand

import com.lighthouse.database.dao.BrandWithSectionDao
import com.lighthouse.database.entity.BrandLocationEntity
import com.lighthouse.database.entity.SectionEntity
import com.lighthouse.domain.Dms
import com.lighthouse.domain.LocationConverter
import com.lighthouse.domain.model.BrandPlaceInfo
import com.lighthouse.mapper.toEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

class BrandLocalDataSourceImpl @Inject constructor(
    private val brandWithSectionDao: BrandWithSectionDao
) : BrandLocalDataSource {

    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO

    override suspend fun getBrands(
        x: Dms,
        y: Dms,
        brandName: String
    ): Result<List<BrandLocationEntity>> {
        val sectionResults =
            brandWithSectionDao.getBrands(
                combineSectionId(
                    x.dmsToString(),
                    y.dmsToString(),
                    brandName
                )
            )
        return if (sectionResults == null) {
            Result.failure(Exception())
        } else {
            Result.success(sectionResults.brands)
        }
    }

    override suspend fun insertBrands(
        brandPlaceInfos: List<BrandPlaceInfo>,
        x: Dms,
        y: Dms,
        brandName: String
    ) = withContext(ioDispatcher) {
        val searchCardinalDirections = LocationConverter.getSearchCardinalDirections(x, y)
        searchCardinalDirections.forEach { location ->
            brandWithSectionDao.insertSection(
                SectionEntity(
                    combineSectionId(location.x.dmsToString(), location.y.dmsToString(), brandName),
                    Date(),
                    location.x,
                    location.y
                )
            )
        }
        brandWithSectionDao.insertBrand(brandPlaceInfos.toEntity())
    }

    override suspend fun isNearBrand(
        x: Dms,
        y: Dms,
        brandName: String
    ): List<BrandLocationEntity>? {
        val sectionResults =
            brandWithSectionDao.getBrands(
                combineSectionId(
                    x.dmsToString(),
                    y.dmsToString(),
                    brandName
                )
            ) ?: return null

        return sectionResults.brands
    }

    override suspend fun removeExpirationBrands() {
        val date: Date = Calendar.getInstance().apply {
            add(Calendar.MONTH, -1)
        }.time
        brandWithSectionDao.removeExpirationBrands(date.time)
    }

    companion object {
        fun combineSectionId(x: String, y: String, brandName: String) =
            "${x}_${y}_${brandName.lowercase()}"
    }
}
