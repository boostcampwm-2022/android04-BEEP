package com.lighthouse.di

import android.content.ContentResolver
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import com.lighthouse.database.BeepDatabase
import com.lighthouse.database.BeepDatabase.Companion.DATABASE_NAME
import com.lighthouse.database.dao.BrandWithSectionDao
import com.lighthouse.database.dao.GifticonCropDao
import com.lighthouse.database.dao.GifticonDao
import com.lighthouse.domain.usecase.setting.GetSecurityOptionUseCase
import com.lighthouse.presentation.ui.security.AuthManager
import com.lighthouse.presentation.util.UserPreference
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    private const val USER_PREFERENCES = "user_preferences"

    @Provides
    @Singleton
    fun provideContentResolver(
        @ApplicationContext context: Context
    ): ContentResolver = context.contentResolver

    @Provides
    @Singleton
    fun provideBeepDatabase(
        @ApplicationContext context: Context
    ): BeepDatabase {
        return Room.databaseBuilder(
            context,
            BeepDatabase::class.java,
            DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideGifticonDao(
        database: BeepDatabase
    ): GifticonDao = database.gifticonDao()

    @Provides
    @Singleton
    fun provideGifticonCropDao(
        database: BeepDatabase
    ): GifticonCropDao = database.gifticonCropDao()

    @Provides
    @Singleton
    fun provideBrandDao(
        database: BeepDatabase
    ): BrandWithSectionDao = database.brandWithSectionDao()

    @Singleton
    @Provides
    fun providePreferencesDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            produceFile = { context.preferencesDataStoreFile(USER_PREFERENCES) }
        )
    }

    @Singleton
    @Provides
    fun provideUserPreference(getSecurityOptionUseCase: GetSecurityOptionUseCase): UserPreference {
        return UserPreference(getSecurityOptionUseCase)
    }

    @Singleton
    @Provides
    fun provideAuthManger(userPreference: UserPreference): AuthManager {
        return AuthManager(userPreference)
    }
}
