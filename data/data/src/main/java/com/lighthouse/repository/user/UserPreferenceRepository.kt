package com.lighthouse.repository.user

import com.lighthouse.beep.model.user.SecurityOption
import kotlinx.coroutines.flow.Flow

interface UserPreferenceRepository {

    suspend fun getIV(userId: String): Result<ByteArray?>

    suspend fun setIV(userId: String, iv: ByteArray): Result<Unit>

    suspend fun setPinPassword(
        userId: String,
        pinPassword: ByteArray
    ): Result<Unit>

    suspend fun confirmPinPassword(
        userId: String,
        pinPassword: ByteArray
    ): Result<Boolean>

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
