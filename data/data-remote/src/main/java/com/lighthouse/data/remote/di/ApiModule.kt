package com.lighthouse.data.remote.di

import com.lighthouse.data.remote.api.KakaoApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Suppress("unused")
@Module
@InstallIn(SingletonComponent::class)
internal object ApiModule {
    @Provides
    @Singleton
    fun provideApiService(
        retrofit: Retrofit
    ): KakaoApiService {
        return retrofit.create(KakaoApiService::class.java)
    }
}
