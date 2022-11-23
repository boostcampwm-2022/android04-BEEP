package com.lighthouse.domain.repository

import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    suspend fun setPin(pinString: String)
    fun getPin(): Flow<String>
}
