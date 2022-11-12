package com.lighthouse.di

import com.lighthouse.datasource.BrandRemoteSource
import com.lighthouse.datasource.BrandRemoteSourceImpl
import com.lighthouse.domain.repository.BrandRepository
import com.lighthouse.repository.BrandRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    @Singleton
    abstract fun bindBrandRemoteDataSource(
        source: BrandRemoteSourceImpl
    ): BrandRemoteSource

    @Binds
    @Singleton
    abstract fun bindBrandRepository(
        repository: BrandRepositoryImpl
    ): BrandRepository
}
