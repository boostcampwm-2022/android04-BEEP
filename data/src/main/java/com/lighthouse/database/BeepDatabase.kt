package com.lighthouse.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.lighthouse.database.converter.DateConverter
import com.lighthouse.database.dao.GifticonDao
import com.lighthouse.database.entity.GifticonEntity

@Database(entities = [GifticonEntity::class], version = 1)
@TypeConverters(DateConverter::class)
abstract class BeepDatabase : RoomDatabase() {
    abstract fun gifticonDao(): GifticonDao

    companion object {
        const val DATABASE_NAME = "beep_database"
    }
}
