package com.lighthouse.di

import android.content.ContentResolver
import android.content.Context
import androidx.room.Room
import com.lighthouse.database.BeepDatabase
import com.lighthouse.database.BeepDatabase.Companion.DATABASE_NAME
import com.lighthouse.database.dao.BrandDao
import com.lighthouse.database.dao.GifticonDao
import com.lighthouse.database.dao.SectionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideContentResolver(
        @ApplicationContext context: Context
    ): ContentResolver = context.contentResolver

    @Provides
    @Singleton
    fun provideBeepDatabase(
        @ApplicationContext context: Context
    ): BeepDatabase {
        return Room.databaseBuilder(
            context,
            BeepDatabase::class.java,
            DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideGifticonDao(
        database: BeepDatabase
    ): GifticonDao = database.gifticonDao()

    @Provides
    @Singleton
    fun provideSectionDao(
        database: BeepDatabase
    ): SectionDao = database.sectionDao()

    @Provides
    @Singleton
    fun provideBrandDao(
        database: BeepDatabase
    ): BrandDao = database.brandDao()
}
