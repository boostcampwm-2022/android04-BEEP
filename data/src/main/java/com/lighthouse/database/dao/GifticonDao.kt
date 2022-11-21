package com.lighthouse.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.lighthouse.database.entity.GifticonEntity
import com.lighthouse.database.entity.GifticonEntity.Companion.GIFTICON_TABLE
import kotlinx.coroutines.flow.Flow

@Dao
interface GifticonDao {

    @Query("SELECT * FROM $GIFTICON_TABLE WHERE id = :id")
    fun getGifticon(id: String): Flow<GifticonEntity>
}
