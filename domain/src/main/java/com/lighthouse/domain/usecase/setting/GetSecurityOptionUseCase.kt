package com.lighthouse.domain.usecase.setting

import com.lighthouse.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSecurityOptionUseCase @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) {
    operator fun invoke(): Flow<Int> {
        return userPreferencesRepository.getSecurityOption()
    }
}
