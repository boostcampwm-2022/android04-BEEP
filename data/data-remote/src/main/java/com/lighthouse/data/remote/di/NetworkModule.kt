package com.lighthouse.data.remote.di

import com.lighthouse.data.remote.utils.HTTPRequestInterceptor
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Suppress("unused")
@Module
@InstallIn(SingletonComponent::class)
internal object NetworkModule {

    private const val KAKAO_URL = "https://dapi.kakao.com/"

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(HTTPRequestInterceptor())
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        moshi: Moshi
    ): Retrofit {
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(KAKAO_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }
}
