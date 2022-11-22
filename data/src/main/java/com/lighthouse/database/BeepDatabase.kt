package com.lighthouse.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.lighthouse.database.converter.DateConverter
import com.lighthouse.database.dao.BrandWithSectionDao
import com.lighthouse.database.dao.GifticonDao
import com.lighthouse.database.entity.BrandLocationEntity
import com.lighthouse.database.entity.GifticonEntity
import com.lighthouse.database.entity.SectionEntity

@Database(entities = [GifticonEntity::class, SectionEntity::class, BrandLocationEntity::class], version = 1)
@TypeConverters(DateConverter::class)
abstract class BeepDatabase : RoomDatabase() {

    abstract fun gifticonDao(): GifticonDao
    abstract fun brandWithSectionDao(): BrandWithSectionDao

    companion object {
        const val DATABASE_NAME = "beep_database"
    }
}
