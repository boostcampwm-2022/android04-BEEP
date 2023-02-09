package com.lighthouse.domain.usecase.setting

import com.lighthouse.domain.repository.user.UserRepository
import javax.inject.Inject

class SaveGuestOptionUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    operator fun invoke(option: Boolean): Result<Unit> {
        return Result.success(Unit)
    }
}
