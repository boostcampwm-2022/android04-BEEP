package com.lighthouse.di

import com.lighthouse.domain.repository.brand.BrandRepository
import com.lighthouse.domain.repository.gallery.GalleryImageRepository
import com.lighthouse.domain.repository.gifticon.GifticonEditRepository
import com.lighthouse.domain.repository.gifticon.GifticonRecognizeRepository
import com.lighthouse.domain.repository.gifticon.GifticonSearchRepository
import com.lighthouse.domain.repository.gifticon.GifticonUsageHistoryRepository
import com.lighthouse.domain.repository.location.LocationRepository
import com.lighthouse.domain.repository.user.UserRepository
import com.lighthouse.repository.brand.BrandRepositoryImpl
import com.lighthouse.repository.gallery.GalleryImageRepositoryImpl
import com.lighthouse.repository.gifticon.GifticonEditRepositoryImpl
import com.lighthouse.repository.gifticon.GifticonRecognizeRepositoryImpl
import com.lighthouse.repository.gifticon.GifticonSearchRepositoryImpl
import com.lighthouse.repository.gifticon.GifticonUsageHistoryRepositoryImpl
import com.lighthouse.repository.location.LocationRepositoryImpl
import com.lighthouse.repository.user.UserRepositoryImpl
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
    abstract fun bindsGalleryImageRepository(
        repository: GalleryImageRepositoryImpl
    ): GalleryImageRepository

    @Binds
    abstract fun bindsGifticonEditRepository(
        repository: GifticonEditRepositoryImpl
    ): GifticonEditRepository

    @Binds
    abstract fun bindsGifticonRecognizeRepository(
        repository: GifticonRecognizeRepositoryImpl
    ): GifticonRecognizeRepository

    @Binds
    abstract fun bindsGifticonSearchRepository(
        repository: GifticonSearchRepositoryImpl
    ): GifticonSearchRepository

    @Binds
    abstract fun bindsGifticonUsageHistoryRepository(
        repository: GifticonUsageHistoryRepositoryImpl
    ): GifticonUsageHistoryRepository

    @Binds
    abstract fun bindsLocationRepository(
        repository: LocationRepositoryImpl
    ): LocationRepository

    @Binds
    abstract fun bindsUserRepository(
        repository: UserRepositoryImpl
    ): UserRepository
}
