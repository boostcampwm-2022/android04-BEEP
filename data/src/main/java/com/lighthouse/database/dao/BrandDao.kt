package com.lighthouse.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.lighthouse.database.entity.BrandEntity

@Dao
interface BrandDao {

    @Query("SELECT * FROM brand_table br JOIN section_table se ON se.min_x = :minX AND se.min_y = :minY WHERE br.id == se.id")
    suspend fun getBrands(minX: String, minY: String): List<BrandEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBrand(vararg brandEntity: BrandEntity)
}
