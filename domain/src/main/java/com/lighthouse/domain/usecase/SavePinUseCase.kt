package com.lighthouse.domain.usecase

import com.lighthouse.domain.repository.UserPreferencesRepository
import javax.inject.Inject

class SavePinUseCase @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) {

    suspend operator fun invoke(pinString: String): Result<Unit> {
        // TODO: 저장 성공 / 실패 여부 알아오기
        userPreferencesRepository.setPin(pinString)
        return Result.success(Unit)
    }
}
