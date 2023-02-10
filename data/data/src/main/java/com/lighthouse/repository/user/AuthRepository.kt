package com.lighthouse.repository.user

import com.lighthouse.beep.model.auth.EncryptData
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    fun isGuest(): Flow<Boolean>

    fun getCurrentUserId(): String

    fun encrypt(alias: String, data: String): Result<EncryptData>

    fun decrypt(alias: String, data: EncryptData): Result<String>
}
