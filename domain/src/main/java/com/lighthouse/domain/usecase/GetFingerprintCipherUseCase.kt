package com.lighthouse.domain.usecase

import com.lighthouse.domain.repository.user.UserRepository
import javax.crypto.Cipher
import javax.inject.Inject

class GetFingerprintCipherUseCase @Inject constructor(
    private val userRepository: UserRepository
) {

    operator fun invoke(): Cipher {
        return userRepository.getFingerprintCipher()
    }
}
