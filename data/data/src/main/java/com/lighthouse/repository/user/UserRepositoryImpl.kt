package com.lighthouse.repository.user

import com.lighthouse.beep.model.user.SecurityOption
import com.lighthouse.domain.repository.user.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class UserRepositoryImpl @Inject constructor(
    private val authRepository: AuthRepository,
    private val userPreferenceRepository: UserPreferenceRepository
) : UserRepository {

    override fun isGuest(): Flow<Boolean> {
        return authRepository.isGuest()
    }

    override suspend fun setPinPassword(pinPassword: ByteArray): Result<Unit> {
        val userId = authRepository.getCurrentUserId()
        return userPreferenceRepository.setPinPassword(userId, pinPassword)
    }

    override suspend fun confirmPinPassword(pinPassword: String): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun setSecurityOption(securityOption: SecurityOption): Result<Unit> {
        TODO("Not yet implemented")
    }

    override fun getSecurityOption(): Flow<SecurityOption> {
        TODO("Not yet implemented")
    }

    override suspend fun setNotificationEnable(enable: Boolean): Result<Unit> {
        TODO("Not yet implemented")
    }

    override fun getNotificationEnable(): Flow<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun transferData(newUserId: String): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun clearData(): Result<Unit> {
        TODO("Not yet implemented")
    }
}
