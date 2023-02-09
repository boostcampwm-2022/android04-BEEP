package com.lighthouse.data.content.di

import com.lighthouse.data.content.repository.GalleryImageContentRepositoryImpl
import com.lighthouse.repository.gallery.GalleryImageContentRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Suppress("unused")
@Module
@InstallIn(SingletonComponent::class)
internal abstract class DataModule {

    @Binds
    abstract fun bindsGalleryImageContentRepository(
        galleryImageContentRepositoryImpl: GalleryImageContentRepositoryImpl
    ): GalleryImageContentRepository
}
