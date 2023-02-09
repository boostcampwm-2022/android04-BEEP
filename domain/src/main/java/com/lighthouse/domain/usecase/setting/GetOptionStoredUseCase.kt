package com.lighthouse.domain.usecase.setting

import com.lighthouse.beep.model.user.UserPreferenceOption
import com.lighthouse.domain.repository.user.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

// 구현 하면서 ...
class GetOptionStoredUseCase @Inject constructor(
    private val userRepository: UserRepository
) {

    operator fun invoke(option: UserPreferenceOption): Flow<Result<Boolean>> {
        return flow {
            emit(Result.success(true))
        }
    }
}
