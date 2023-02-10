package com.lighthouse.domain.usecase

import com.lighthouse.domain.repository.user.UserRepository
import javax.inject.Inject

class SaveFilterExpiredUseCase @Inject constructor(
    private val userRepository: UserRepository
) {

    suspend operator fun invoke(enable: Boolean): Result<Unit> {
        return userRepository.setFilterExpired(enable)
    }
}
