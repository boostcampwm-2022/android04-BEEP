package com.lighthouse.domain.usecase.setting

import com.lighthouse.domain.repository.user.UserRepository
import javax.inject.Inject

class RemoveUserDataUseCase @Inject constructor(
    private val userRepository: UserRepository
) {

    suspend operator fun invoke(): Result<Unit> {
        return userRepository.clearData()
    }
}
