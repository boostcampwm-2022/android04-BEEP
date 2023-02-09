package com.lighthouse.repository.user

import kotlinx.coroutines.flow.Flow
import javax.crypto.spec.IvParameterSpec

interface AuthRepository {

    fun isGuest(): Flow<Boolean>

    fun getCurrentUserId(): String

    fun createIV(): ByteArray

    fun encrypt(pin: String, iv: IvParameterSpec)
}
