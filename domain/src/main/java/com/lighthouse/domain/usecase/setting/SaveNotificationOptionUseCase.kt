package com.lighthouse.domain.usecase.setting

import com.lighthouse.domain.repository.user.UserRepository
import javax.inject.Inject

class SaveNotificationOptionUseCase @Inject constructor(
    private val userRepository: UserRepository
) {

    suspend operator fun invoke(enable: Boolean): Result<Unit> {
        return userRepository.setNotificationEnable(enable)
    }
}
