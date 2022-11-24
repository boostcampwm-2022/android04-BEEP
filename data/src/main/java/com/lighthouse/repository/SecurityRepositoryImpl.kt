package com.lighthouse.repository

import com.lighthouse.domain.repository.SecurityRepository
import com.lighthouse.util.CryptoObjectHelper
import javax.crypto.Cipher

class SecurityRepositoryImpl : SecurityRepository {
    override fun getFingerprintCipher(): Cipher {
        return CryptoObjectHelper.getFingerprintCipher()
    }
}
