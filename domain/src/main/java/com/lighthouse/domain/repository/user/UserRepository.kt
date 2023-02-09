package com.lighthouse.domain.repository.user

import com.lighthouse.beep.model.user.SecurityOption
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    fun isGuest(): Flow<Boolean>

    suspend fun setPinPassword(pinPassword: ByteArray): Result<Unit>
    suspend fun confirmPinPassword(pinPassword: String): Result<Unit>

    suspend fun setSecurityOption(securityOption: SecurityOption): Result<Unit>
    fun getSecurityOption(): Flow<SecurityOption>

    suspend fun setNotificationEnable(enable: Boolean): Result<Unit>
    fun getNotificationEnable(): Flow<Boolean>

    suspend fun transferData(newUserId: String): Result<Unit>
    suspend fun clearData(): Result<Unit>
}
