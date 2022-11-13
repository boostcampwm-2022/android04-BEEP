package com.lighthouse.di

import android.content.ContentResolver
import android.content.Context
import com.lighthouse.datasource.BrandRemoteSource
import com.lighthouse.datasource.BrandRemoteSourceImpl
import com.lighthouse.datasource.GalleryImageLocalSource
import com.lighthouse.datasource.GalleryImageLocalSourceImpl
import com.lighthouse.domain.repository.BrandRepository
import com.lighthouse.domain.repository.GalleryImageRepository
import com.lighthouse.repository.BrandRepositoryImpl
import com.lighthouse.repository.GalleryImageRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
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

    @Binds
    @Singleton
    abstract fun bindGalleryLocalDataSource(
        source: GalleryImageLocalSourceImpl
    ): GalleryImageLocalSource

    @Binds
    @Singleton
    abstract fun bindGalleryImageRepository(
        repository: GalleryImageRepositoryImpl
    ): GalleryImageRepository
}
