package com.lighthouse.di

import android.content.ContentResolver
import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Suppress("unused")
@Module
@InstallIn(SingletonComponent::class)
object ContentModule {

    @Provides
    fun provideContentResolver(
        @ApplicationContext context: Context
    ): ContentResolver {
        return context.contentResolver
    }
}
