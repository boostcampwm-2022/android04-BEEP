package com.lighthouse.domain.repository.user

import com.lighthouse.beep.model.user.SecurityOption
import kotlinx.coroutines.flow.Flow
import javax.crypto.Cipher

interface UserRepository {

    fun getUserId(): String
    fun isGuest(): Flow<Boolean>

    fun getFingerprintCipher(): Cipher

    suspend fun setPinPassword(pinPassword: String): Result<Unit>
    suspend fun confirmPinPassword(pinPassword: String): Result<Boolean>

    suspend fun setSecurityOption(securityOption: SecurityOption): Result<Unit>
    fun getSecurityOption(): Flow<Result<SecurityOption>>

    suspend fun setNotificationEnable(enable: Boolean): Result<Unit>
    fun getNotificationEnable(): Flow<Result<Boolean>>

    suspend fun transferData(newUserId: String): Result<Unit>
    suspend fun clearData(): Result<Unit>
}
