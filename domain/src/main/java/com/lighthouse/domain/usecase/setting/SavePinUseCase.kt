package com.lighthouse.domain.usecase.setting

import com.lighthouse.domain.repository.UserPreferencesRepository
import javax.inject.Inject

class SavePinUseCase @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) {

    suspend operator fun invoke(pinString: String): Result<Unit> {
        return userPreferencesRepository.setPinString(pinString)
    }
}
