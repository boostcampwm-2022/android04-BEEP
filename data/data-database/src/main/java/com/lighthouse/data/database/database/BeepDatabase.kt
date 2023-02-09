package com.lighthouse.data.database.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.lighthouse.data.database.converter.DateConverter
import com.lighthouse.data.database.converter.DmsConverter
import com.lighthouse.data.database.converter.RectConverter
import com.lighthouse.data.database.converter.UriConverter
import com.lighthouse.data.database.dao.BrandLocationDao
import com.lighthouse.data.database.dao.GifticonEditDao
import com.lighthouse.data.database.dao.GifticonSearchDao
import com.lighthouse.data.database.dao.GifticonUsageHistoryDao
import com.lighthouse.data.database.entity.DBBrandLocationEntity
import com.lighthouse.data.database.entity.DBGifticonCropEntity
import com.lighthouse.data.database.entity.DBGifticonEntity
import com.lighthouse.data.database.entity.DBSectionEntity
import com.lighthouse.data.database.entity.DBUsageHistoryEntity

@Database(
    entities = [
        DBGifticonEntity::class,
        DBGifticonCropEntity::class,
        DBSectionEntity::class,
        DBBrandLocationEntity::class,
        DBUsageHistoryEntity::class
    ],
    version = 1
)
@TypeConverters(
    DateConverter::class,
    DmsConverter::class,
    RectConverter::class,
    UriConverter::class
)
internal abstract class BeepDatabase : RoomDatabase() {

    abstract fun gifticonSearchDao(): GifticonSearchDao

    abstract fun gifticonEditDao(): GifticonEditDao

    abstract fun gifticonUsageHistoryDao(): GifticonUsageHistoryDao

    abstract fun brandLocationDao(): BrandLocationDao

    companion object {
        const val DATABASE_NAME = "beep_database"
    }
}
