package com.lighthouse.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.lighthouse.database.entity.BrandLocationEntity
import com.lighthouse.database.entity.BrandWithSections
import com.lighthouse.database.entity.SectionEntity

@Dao
interface BrandWithSectionDao {

    @Query("SELECT * FROM section_table WHERE section_id =:sectionId")
    suspend fun getBrands(sectionId: String): BrandWithSections?

    @Query("SELECT section_id FROM section_table WHERE section_id =:sectionId")
    suspend fun getSectionId(sectionId: String): String?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSection(sectionEntity: SectionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBrand(brands: List<BrandLocationEntity>)

    @Query("DELETE FROM section_table WHERE section_id =:sectionId")
    suspend fun deleteSection(sectionId: String)
}
