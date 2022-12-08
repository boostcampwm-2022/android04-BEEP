package com.lighthouse.di

import android.content.Context
import com.lighthouse.datasource.location.SharedLocationManager
import com.lighthouse.presentation.background.NotificationHelper
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
    fun provideSharedLocationManager(
        @ApplicationContext context: Context
    ): SharedLocationManager = SharedLocationManager(context)

    @Provides
    @Singleton
    fun provideNotificationHelper(
        @ApplicationContext context: Context
    ): NotificationHelper = NotificationHelper(context)
}
