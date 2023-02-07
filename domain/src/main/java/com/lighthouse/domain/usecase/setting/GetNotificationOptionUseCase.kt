package com.lighthouse.domain.usecase.setting

import com.lighthouse.beep.model.user.UserPreferenceOption
import com.lighthouse.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetNotificationOptionUseCase @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) {
    operator fun invoke(): Flow<Boolean> {
        return userPreferencesRepository.getBooleanOption(UserPreferenceOption.NOTIFICATION)
    }
}
