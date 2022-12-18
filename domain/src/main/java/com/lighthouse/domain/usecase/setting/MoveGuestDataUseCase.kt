package com.lighthouse.domain.usecase.setting

import com.lighthouse.domain.repository.UserPreferencesRepository
import javax.inject.Inject

class MoveGuestDataUseCase @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) {
    suspend operator fun invoke(uid: String): Result<Unit> {
        return userPreferencesRepository.moveGuestData(uid)
    }
}
