package com.lighthouse.domain.repository

import javax.crypto.Cipher

interface SecurityRepository {

    fun getFingerprintCipher(): Cipher
}
