package com.lighthouse.data.database.di

import com.lighthouse.data.database.repository.brand.BrandDatabaseRepositoryImpl
import com.lighthouse.data.database.repository.gifticon.GifticonEditDatabaseRepositoryImpl
import com.lighthouse.data.database.repository.gifticon.GifticonSearchDatabaseRepositoryImpl
import com.lighthouse.data.database.repository.gifticon.GifticonUsageHistoryDatabaseRepositoryImpl
import com.lighthouse.repository.brand.BrandDatabaseRepository
import com.lighthouse.repository.gifticon.GifticonEditDatabaseRepository
import com.lighthouse.repository.gifticon.GifticonSearchDatabaseRepository
import com.lighthouse.repository.gifticon.GifticonUsageHistoryDatabaseRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Suppress("unused")
@Module
@InstallIn(SingletonComponent::class)
internal abstract class DataModule {

    @Binds
    abstract fun bindsDatabaseRepository(
        repository: BrandDatabaseRepositoryImpl
    ): BrandDatabaseRepository

    @Binds
    abstract fun bindsGifticonEditRepository(
        repository: GifticonEditDatabaseRepositoryImpl
    ): GifticonEditDatabaseRepository

    @Binds
    abstract fun bindsGifticonSearchRepository(
        repository: GifticonSearchDatabaseRepositoryImpl
    ): GifticonSearchDatabaseRepository

    @Binds
    abstract fun bindsGifticonUsageHistoryRepository(
        repository: GifticonUsageHistoryDatabaseRepositoryImpl
    ): GifticonUsageHistoryDatabaseRepository
}
