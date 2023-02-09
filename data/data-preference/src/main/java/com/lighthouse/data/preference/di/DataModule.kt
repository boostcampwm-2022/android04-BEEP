package com.lighthouse.data.preference.di

import com.lighthouse.data.preference.repository.UserPreferenceRepositoryImpl
import com.lighthouse.repository.user.UserPreferenceRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Suppress("unused")
@Module
@InstallIn(SingletonComponent::class)
internal abstract class DataModule {

    @Binds
    abstract fun bindsUserPreferenceRepository(
        repository: UserPreferenceRepositoryImpl
    ): UserPreferenceRepository
}
