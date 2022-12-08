package com.lighthouse.domain.usecase.setting

import com.lighthouse.domain.repository.UserPreferencesRepository
import javax.inject.Inject

class RemoveUserDataUseCase @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        return userPreferencesRepository.removeCurrentUserData()
    }
}
