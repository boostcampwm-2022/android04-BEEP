package com.lighthouse.repository

import com.lighthouse.core.android.utils.crypto.CryptoObjectHelper
import com.lighthouse.domain.repository.SecurityRepository
import javax.crypto.Cipher

class SecurityRepositoryImpl : SecurityRepository {
    override fun getFingerprintCipher(): Cipher {
        return CryptoObjectHelper.getFingerprintCipher()
    }
}
