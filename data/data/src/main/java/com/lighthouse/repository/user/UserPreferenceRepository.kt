package com.lighthouse.repository.user

import com.lighthouse.beep.model.auth.EncryptData
import com.lighthouse.beep.model.user.SecurityOption
import kotlinx.coroutines.flow.Flow

interface UserPreferenceRepository {

    suspend fun setEncryptData(userId: String, encryptData: EncryptData): Result<Unit>

    suspend fun getEncryptData(userId: String): Result<EncryptData>

    suspend fun setSecurityOption(
        userId: String,
        securityOption: SecurityOption
    ): Result<Unit>

    fun getSecurityOption(userId: String): Flow<Result<SecurityOption>>

    suspend fun setNotificationEnable(
        userId: String,
        enable: Boolean
    ): Result<Unit>

    fun getNotificationEnable(userId: String): Flow<Result<Boolean>>

    suspend fun setFilterExpired(
        userId: String,
        filterExpired: Boolean
    ): Result<Unit>

    fun getFilterExpired(userId: String): Flow<Result<Boolean>>

    suspend fun transferData(
        oldUserId: String,
        newUserId: String
    ): Result<Unit>

    suspend fun clearData(userId: String): Result<Unit>
}
