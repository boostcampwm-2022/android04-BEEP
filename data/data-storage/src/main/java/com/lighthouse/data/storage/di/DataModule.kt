package com.lighthouse.data.storage.di

import com.lighthouse.data.storage.repository.GifticonStorageRepositoryImpl
import com.lighthouse.repository.gifticon.GifticonStorageRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Suppress("unused")
@Module
@InstallIn(SingletonComponent::class)
internal abstract class DataModule {

    @Binds
    abstract fun bindsGifticonStorageRepository(
        repository: GifticonStorageRepositoryImpl
    ): GifticonStorageRepository
}
