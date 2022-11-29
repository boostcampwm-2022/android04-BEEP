package com.lighthouse.di

import android.content.Context
import com.lighthouse.datasource.gifticon.GifticonImageSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ProviderModule {

    @Provides
    @Singleton
    fun provideGifticonImageSource(
        @ApplicationContext context: Context
    ): GifticonImageSource = GifticonImageSource(context)
}
