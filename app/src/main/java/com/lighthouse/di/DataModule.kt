package com.lighthouse.di

import com.lighthouse.datasource.GalleryImageLocalSource
import com.lighthouse.datasource.GalleryImageLocalSourceImpl
import com.lighthouse.datasource.brand.BrandLocalDataSource
import com.lighthouse.datasource.brand.BrandLocalDataSourceImpl
import com.lighthouse.datasource.brand.BrandRemoteDataSource
import com.lighthouse.datasource.brand.BrandRemoteDataSourceImpl
import com.lighthouse.datasource.gifticon.GifticonLocalDataSource
import com.lighthouse.datasource.gifticon.GifticonLocalDataSourceImpl
import com.lighthouse.domain.repository.BrandRepository
import com.lighthouse.domain.repository.GalleryImageRepository
import com.lighthouse.domain.repository.LocationRepository
import com.lighthouse.repository.BrandRepositoryImpl
import com.lighthouse.repository.GalleryImageRepositoryImpl
import com.lighthouse.repository.LocationRepositoryImpl
import com.lighthouse.domain.repository.GifticonRepository
import com.lighthouse.repository.BrandRepositoryImpl
import com.lighthouse.repository.GalleryImageRepositoryImpl
import com.lighthouse.repository.GifticonRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SingletonScopedDataModule {

    @Binds
    @Singleton
    abstract fun bindBrandRemoteDataSource(
        source: BrandRemoteDataSourceImpl
    ): BrandRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindBrandLocalDataSource(
        source: BrandLocalDataSourceImpl
    ): BrandLocalDataSource

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

    @Binds
    @Singleton
    abstract fun bindGifticonLocalDataSource(
        source: GifticonLocalDataSourceImpl
    ): GifticonLocalDataSource

    @Binds
    @Singleton
    abstract fun bindGifticonRepository(
        repository: GifticonRepositoryImpl
    ): GifticonRepository
}

@Module
@InstallIn(ActivityRetainedComponent::class)
abstract class ViewModelScopedDataModule {

    @Binds
    @ActivityRetainedScoped
    abstract fun bindLocationRepository(
        repository: LocationRepositoryImpl
    ): LocationRepository
}
