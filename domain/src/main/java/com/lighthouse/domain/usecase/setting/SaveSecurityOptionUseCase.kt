package com.lighthouse.domain.usecase.setting

import com.lighthouse.domain.model.UserPreferenceOption
import com.lighthouse.domain.repository.UserPreferencesRepository
import javax.inject.Inject

class SaveSecurityOptionUseCase @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) {
    suspend operator fun invoke(option: Int) {
        userPreferencesRepository.setIntOption(UserPreferenceOption.SECURITY, option)
    }
}
