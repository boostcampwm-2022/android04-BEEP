package com.lighthouse.domain.usecase.setting

import com.lighthouse.domain.model.UserPreferenceOption
import com.lighthouse.domain.repository.UserPreferencesRepository
import javax.inject.Inject

class SaveNotificationOptionUseCase @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) {

    suspend operator fun invoke(option: Boolean): Result<Unit> {
        return userPreferencesRepository.setBooleanOption(UserPreferenceOption.NOTIFICATION, option)
    }
}
