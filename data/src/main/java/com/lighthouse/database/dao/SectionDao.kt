package com.lighthouse.database.dao

import androidx.room.Dao
import androidx.room.Insert
import com.lighthouse.database.entity.SectionEntity

@Dao
interface SectionDao {

    @Insert
    suspend fun insertSection(sectionEntity: SectionEntity)
}
