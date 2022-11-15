package com.lighthouse.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.lighthouse.database.dao.GifticonDao
import com.lighthouse.database.entity.GifticonEntity

@Database(entities = [GifticonEntity::class], version = 1)
abstract class BeepDatabase : RoomDatabase() {
    abstract fun gifticonDao(): GifticonDao

    companion object {
        const val DATABASE_NAME = "beep_database"
    }
}
