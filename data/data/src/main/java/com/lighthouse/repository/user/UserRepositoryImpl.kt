package com.lighthouse.repository.user

import com.lighthouse.beep.model.user.SecurityOption
import com.lighthouse.domain.repository.user.UserRepository
import kotlinx.coroutines.flow.Flow
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

    override suspend fun setPinPassword(pinPassword: String): Result<Unit> = runCatching {
        val userId = authRepository.getCurrentUserId()
        val encryptData = authRepository.encrypt(SecurityOption.PIN.name, pinPassword).getOrThrow()
        userPreferenceRepository.setEncryptData(userId, encryptData).getOrThrow()
    }

    override suspend fun confirmPinPassword(pinPassword: String): Result<Boolean> = runCatching {
        val userId = authRepository.getCurrentUserId()
        val encryptData = userPreferenceRepository.getEncryptData(userId).getOrThrow()
        val decryptData = authRepository.decrypt(SecurityOption.PIN.name, encryptData).getOrThrow()
        pinPassword == decryptData
    }

    override suspend fun setSecurityOption(securityOption: SecurityOption): Result<Unit> {
        val userId = authRepository.getCurrentUserId()
        return userPreferenceRepository.setSecurityOption(userId, securityOption)
    }

    override fun getSecurityOption(): Flow<Result<SecurityOption>> {
        val userId = authRepository.getCurrentUserId()
        return userPreferenceRepository.getSecurityOption(userId)
    }

    override suspend fun setNotificationEnable(enable: Boolean): Result<Unit> {
        val userId = authRepository.getCurrentUserId()
        return userPreferenceRepository.setNotificationEnable(userId, enable)
    }

    override fun getNotificationEnable(): Flow<Result<Boolean>> {
        val userId = authRepository.getCurrentUserId()
        return userPreferenceRepository.getNotificationEnable(userId)
    }

    override suspend fun transferData(newUserId: String): Result<Unit> {
        val userId = authRepository.getCurrentUserId()
        return userPreferenceRepository.transferData(userId, newUserId)
    }

    override suspend fun clearData(): Result<Unit> {
        val userId = authRepository.getCurrentUserId()
        return userPreferenceRepository.clearData(userId)
    }
}
