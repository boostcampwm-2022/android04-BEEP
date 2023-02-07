package com.lighthouse.domain.usecase.setting

import com.lighthouse.beep.model.user.UserPreferenceOption
import com.lighthouse.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetOptionStoredUseCase @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) {
    operator fun invoke(option: UserPreferenceOption): Flow<Boolean> {
        return userPreferencesRepository.isStored(option)
    }
}
