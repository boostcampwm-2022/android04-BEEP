package com.lighthouse.domain.usecase

import com.lighthouse.domain.repository.SecurityRepository
import javax.crypto.Cipher
import javax.inject.Inject

class GetFingerprintCipherUseCase @Inject constructor(
    private val securityRepository: SecurityRepository
) {

    operator fun invoke(): Cipher {
        return securityRepository.getFingerprintCipher()
    }
}
