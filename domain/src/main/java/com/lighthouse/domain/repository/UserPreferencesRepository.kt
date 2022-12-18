package com.lighthouse.domain.repository

import com.lighthouse.domain.model.UserPreferenceOption
import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    suspend fun setPinString(pinString: String): Result<Unit>
    fun getPinString(): Flow<String>

    suspend fun setSecurityOption(value: Int): Result<Unit>
    fun getSecurityOption(): Flow<Int>

    suspend fun setBooleanOption(option: UserPreferenceOption, value: Boolean): Result<Unit>
    fun getBooleanOption(option: UserPreferenceOption): Flow<Boolean>

    fun isStored(option: UserPreferenceOption): Flow<Boolean>

    suspend fun moveGuestData(uid: String): Result<Unit>
    suspend fun removeCurrentUserData(): Result<Unit>
}
