package com.lighthouse.data.database.di

import android.content.Context
import androidx.room.Room
import com.lighthouse.data.database.BeepDatabase
import com.lighthouse.data.database.dao.BrandLocationDao
import com.lighthouse.data.database.dao.GifticonEditDao
import com.lighthouse.data.database.dao.GifticonSearchDao
import com.lighthouse.data.database.dao.GifticonUsageHistoryDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Suppress("unused")
@Module
@InstallIn(SingletonComponent::class)
internal object DataBaseModule {

    @Provides
    fun provideBeepDatabase(
        @ApplicationContext context: Context
    ): BeepDatabase {
        return Room.databaseBuilder(
            context,
            BeepDatabase::class.java,
            BeepDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    fun provideGifticonSearchDao(
        database: BeepDatabase
    ): GifticonSearchDao = database.gifticonSearchDao()

    @Provides
    fun provideGifticonEditDao(
        database: BeepDatabase
    ): GifticonEditDao = database.gifticonEditDao()

    @Provides
    fun provideGifticonUsageHistoryDao(
        database: BeepDatabase
    ): GifticonUsageHistoryDao = database.gifticonUsageHistoryDao()

    @Provides
    fun provideBrandLocationDao(
        database: BeepDatabase
    ): BrandLocationDao = database.brandLocationDao()
}
