package com.lighthouse.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.lighthouse.database.entity.GifticonEntity.Companion.GIFTICON_TABLE
import com.lighthouse.domain.model.Gifticon

@Dao
interface GifticonDao {

    @Query("SELECT * FROM $GIFTICON_TABLE WHERE id = :id")
    fun getGifticon(id: String): Gifticon
}
