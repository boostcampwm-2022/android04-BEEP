package com.lighthouse.domain.usecase.setting

import com.lighthouse.beep.model.user.SecurityOption
import com.lighthouse.domain.repository.user.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSecurityOptionUseCase @Inject constructor(
    private val userRepository: UserRepository
) {

    operator fun invoke(): Flow<Result<SecurityOption>> {
        return userRepository.getSecurityOption()
    }
}
