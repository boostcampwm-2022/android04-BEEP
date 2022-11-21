package com.lighthouse.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.lighthouse.database.entity.BrandEntity
import com.lighthouse.database.entity.BrandWithSections
import com.lighthouse.database.entity.SectionEntity
import com.lighthouse.domain.model.BrandPlaceInfo
import com.lighthouse.mapper.toEntity

@Dao
interface BrandWithSectionDao {

    @Query("SELECT * FROM section_table WHERE section_id =:sectionId")
    suspend fun getBrands(sectionId: Long): List<BrandWithSections>

    @Query("SELECT section_id FROM section_table WHERE min_x =:minX AND min_y = :minY")
    suspend fun getSectionId(minX: String, minY: String): Long?

    @Transaction
    suspend fun insertSectionWithBrands(sectionEntity: SectionEntity, brands: List<BrandPlaceInfo>) {
        val sectionId = insertSection(sectionEntity)
        insertBrand(brands.toEntity(sectionId))
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSection(sectionEntity: SectionEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBrand(brandEntity: List<BrandEntity>)

    @Query("DELETE FROM section_table WHERE section_id =:sectionId")
    suspend fun deleteSection(sectionId: Long)
}
