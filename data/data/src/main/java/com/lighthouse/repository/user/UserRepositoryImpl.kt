package com.lighthouse.repository.user

import com.lighthouse.beep.model.user.SecurityOption
import com.lighthouse.domain.repository.user.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.crypto.Cipher
import javax.inject.Inject

internal class UserRepositoryImpl @Inject constructor(
    private val authRepository: AuthRepository,
    private val userPreferenceRepository: UserPreferenceRepository
) : UserRepository {

    override fun getUserId(): String {
        return authRepository.getCurrentUserId()
    }

    override fun isGuest(): Flow<Boolean> {
        return authRepository.isGuest()
    }

    override fun getFingerprintCipher(): Cipher {
        TODO("Not yet implemented")
    }

    override suspend fun setPinPassword(pinPassword: String): Result<Unit> {
        val userId = authRepository.getCurrentUserId()
        return userPreferenceRepository.setPinPassword(userId, pinPassword.toByteArray())
    }

    override suspend fun confirmPinPassword(pinPassword: String): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun setSecurityOption(securityOption: SecurityOption): Result<Unit> {
        TODO("Not yet implemented")
    }

    override fun getSecurityOption(): Flow<Result<SecurityOption>> {
        TODO("Not yet implemented")
    }

    override suspend fun setNotificationEnable(enable: Boolean): Result<Unit> {
        TODO("Not yet implemented")
    }

    override fun getNotificationEnable(): Flow<Result<Boolean>> {
        TODO("Not yet implemented")
    }

    override suspend fun transferData(newUserId: String): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun clearData(): Result<Unit> {
        TODO("Not yet implemented")
    }
}
