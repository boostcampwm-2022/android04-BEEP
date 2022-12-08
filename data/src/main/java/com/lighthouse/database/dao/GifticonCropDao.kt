package com.lighthouse.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.lighthouse.database.entity.GifticonCropEntity
import com.lighthouse.database.entity.GifticonCropEntity.Companion.GIFTICON_CROP_TABLE
import kotlinx.coroutines.flow.Flow

@Dao
interface GifticonCropDao {
    @Query("SELECT * FROM $GIFTICON_CROP_TABLE WHERE gifticon_id = :id LIMIT 1")
    fun getGifticonCrop(id: String): Flow<GifticonCropEntity>

    @Update
    suspend fun updateGifticonCrop(cropEntity: GifticonCropEntity)

    @Insert
    suspend fun insertGifticonCrop(cropEntity: GifticonCropEntity)
}
