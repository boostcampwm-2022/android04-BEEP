package com.lighthouse.domain.usecase.setting

import com.lighthouse.domain.repository.user.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetGuestOptionUseCase @Inject constructor(
    private val userRepository: UserRepository
) {

    operator fun invoke(): Flow<Boolean> {
        return userRepository.isGuest()
    }
}
