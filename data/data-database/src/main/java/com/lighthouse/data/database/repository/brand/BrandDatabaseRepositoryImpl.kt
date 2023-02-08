package com.lighthouse.data.database.repository.brand

import com.lighthouse.beep.model.brand.BrandPlaceInfo
import com.lighthouse.beep.model.location.Dms
import com.lighthouse.beep.model.result.DbResult
import com.lighthouse.data.database.dao.BrandLocationDao
import com.lighthouse.data.database.ext.runCatchingDB
import com.lighthouse.data.database.mapper.brand.combineSectionId
import com.lighthouse.data.database.mapper.brand.toDomain
import com.lighthouse.data.database.mapper.brand.toEntity
import com.lighthouse.repository.brand.BrandDatabaseRepository
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

internal class BrandDatabaseRepositoryImpl @Inject constructor(
    private val brandLocationDao: BrandLocationDao
) : BrandDatabaseRepository {

    override suspend fun getBrands(
        x: Dms,
        y: Dms,
        brandName: String
    ): DbResult<List<BrandPlaceInfo>> {
        val sections = brandLocationDao.getBrands(combineSectionId(x, y, brandName))
        return if (sections != null) {
            runCatchingDB {
                sections.brands.toDomain()
            }
        } else {
            DbResult.Failure(Exception())
        }
    }

    override suspend fun insertBrands(
        brandPlaceInfoList: List<BrandPlaceInfo>,
        x: Dms,
        y: Dms,
        brandName: String
    ): DbResult<Unit> {
        return runCatchingDB {
            brandLocationDao.insertBrand(brandPlaceInfoList.toEntity(), x, y, brandName)
        }
    }

    override suspend fun removeExpirationBrands(): DbResult<Unit> {
        val date: Date = Calendar.getInstance().apply {
            add(Calendar.MONTH, -1)
        }.time
        return runCatchingDB {
            brandLocationDao.removeExpirationBrands(date.time)
        }
    }
}
