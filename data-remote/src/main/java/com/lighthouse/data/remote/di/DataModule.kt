package com.lighthouse.data.remote.di

import com.lighthouse.data.remote.repository.BrandRemoteRepositoryImpl
import com.lighthouse.repository.brand.BrandRemoteRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Suppress("unused")
@Module
@InstallIn(SingletonComponent::class)
internal abstract class DataModule {

    @Binds
    abstract fun bindsRemoteRepository(
        repository: BrandRemoteRepositoryImpl
    ): BrandRemoteRepository
}
