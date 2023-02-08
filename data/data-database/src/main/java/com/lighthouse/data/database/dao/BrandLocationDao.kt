package com.lighthouse.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.lighthouse.beep.model.location.Dms
import com.lighthouse.common.utils.geography.LocationConverter
import com.lighthouse.data.database.entity.DBBrandLocationEntity
import com.lighthouse.data.database.entity.DBSectionEntity
import com.lighthouse.data.database.mapper.brand.combineSectionId
import com.lighthouse.data.database.model.DBBrandWithSections
import java.util.Date

@Dao
internal interface BrandLocationDao {

    /**
     * 특정 섹션의 BrandLocation 정보 가져오기
     * 1. SectionId
     * */
    @Query(
        "SELECT * FROM section_table " +
            "WHERE section_id =:sectionId"
    )
    suspend fun getBrands(sectionId: String): DBBrandWithSections?

    /**
     * BrandLocation 정보를 추가
     * 1. BrandLocation List
     * */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBrand(brands: List<DBBrandLocationEntity>)

    /**
     * section 정보를 추가
     * 1. Section
     * */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSection(sectionEntity: DBSectionEntity)

    /**
     * brandLocation과 section 정보를 추가
     * 1. brandLocation List
     * 2. x Dms
     * 3. y Dms
     * 4. 브랜드 이름
     * */
    @Transaction
    suspend fun insertBrand(
        brands: List<DBBrandLocationEntity>,
        x: Dms,
        y: Dms,
        brandName: String
    ) {
        val directions = LocationConverter.getSearchCardinalDirections(x, y)
        directions.forEach { location ->
            val sectionId = combineSectionId(x, y, brandName)
            insertSection(DBSectionEntity(sectionId, Date(), location.x, location.y))
        }
        insertBrand(brands)
    }

    /**
     * 기준 시간 이하의 Section 정보 삭제
     * 1. 기준 시간
     * */
    @Query(
        "DELETE FROM section_table " +
            "WHERE search_date <= :time"
    )
    suspend fun removeExpirationBrands(time: Long)
}
