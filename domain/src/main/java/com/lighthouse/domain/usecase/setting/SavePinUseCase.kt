package com.lighthouse.domain.usecase.setting

import com.lighthouse.domain.repository.user.UserRepository
import javax.inject.Inject

class SavePinUseCase @Inject constructor(
    private val userRepository: UserRepository
) {

    suspend operator fun invoke(pinString: String): Result<Unit> {
        return userRepository.setPinPassword(pinString)
    }
}
