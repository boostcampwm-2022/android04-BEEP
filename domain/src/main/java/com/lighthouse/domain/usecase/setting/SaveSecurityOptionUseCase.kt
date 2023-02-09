package com.lighthouse.domain.usecase.setting

import com.lighthouse.beep.model.user.SecurityOption
import com.lighthouse.domain.repository.user.UserRepository
import javax.inject.Inject

class SaveSecurityOptionUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(option: SecurityOption): Result<Unit> {
        return userRepository.setSecurityOption(option)
    }
}
