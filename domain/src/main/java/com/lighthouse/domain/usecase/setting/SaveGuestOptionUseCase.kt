package com.lighthouse.domain.usecase.setting

import com.lighthouse.domain.model.UserPreferenceOption
import com.lighthouse.domain.repository.UserPreferencesRepository
import javax.inject.Inject

class SaveGuestOptionUseCase @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) {
    suspend operator fun invoke(option: Boolean): Result<Unit> {
        return userPreferencesRepository.setBooleanOption(UserPreferenceOption.GUEST, option)
    }
}
