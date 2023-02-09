package com.lighthouse.domain.usecase.setting

import com.lighthouse.domain.repository.user.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetNotificationOptionUseCase @Inject constructor(
    private val userRepository: UserRepository
) {

    operator fun invoke(): Flow<Result<Boolean>> {
        return userRepository.getNotificationEnable()
    }
}
