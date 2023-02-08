package com.lighthouse.di

import com.lighthouse.domain.repository.BrandRepository
import com.lighthouse.domain.repository.GifticonEditRepository
import com.lighthouse.domain.repository.GifticonSearchRepository
import com.lighthouse.domain.repository.GifticonUsageHistoryRepository
import com.lighthouse.repository.brand.BrandRepositoryImpl
import com.lighthouse.repository.gifticon.GifticonEditRepositoryImpl
import com.lighthouse.repository.gifticon.GifticonSearchRepositoryImpl
import com.lighthouse.repository.gifticon.GifticonUsageHistoryRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Suppress("unused")
@Module
@InstallIn(SingletonComponent::class)
internal abstract class DataModule {

    @Binds
    abstract fun bindsBrandRepository(
        repository: BrandRepositoryImpl
    ): BrandRepository

    @Binds
    abstract fun bindsGifticonEditRepository(
        repository: GifticonEditRepositoryImpl
    ): GifticonEditRepository

    @Binds
    abstract fun bindsGifticonSearchRepository(
        repository: GifticonSearchRepositoryImpl
    ): GifticonSearchRepository

    @Binds
    abstract fun bindsGifticonUsageHistoryRepository(
        repository: GifticonUsageHistoryRepositoryImpl
    ): GifticonUsageHistoryRepository
}
