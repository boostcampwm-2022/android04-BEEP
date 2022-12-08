package com.lighthouse.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.lighthouse.database.converter.DateConverter
import com.lighthouse.database.converter.DmsConverter
import com.lighthouse.database.converter.RectConverter
import com.lighthouse.database.dao.BrandWithSectionDao
import com.lighthouse.database.dao.GifticonCropDao
import com.lighthouse.database.dao.GifticonDao
import com.lighthouse.database.entity.BrandLocationEntity
import com.lighthouse.database.entity.GifticonCropEntity
import com.lighthouse.database.entity.GifticonEntity
import com.lighthouse.database.entity.SectionEntity
import com.lighthouse.database.entity.UsageHistoryEntity

@Database(
    entities = [
        GifticonEntity::class,
        GifticonCropEntity::class,
        SectionEntity::class,
        BrandLocationEntity::class,
        UsageHistoryEntity::class
    ],
    version = 1
)
@TypeConverters(
    DateConverter::class,
    DmsConverter::class,
    RectConverter::class
)
abstract class BeepDatabase : RoomDatabase() {

    abstract fun gifticonDao(): GifticonDao
    abstract fun gifticonCropDao(): GifticonCropDao
    abstract fun brandWithSectionDao(): BrandWithSectionDao

    companion object {
        const val DATABASE_NAME = "beep_database"
    }
}
