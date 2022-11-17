package com.lighthouse.presentation.ui.security.fingerprint

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyPermanentlyInvalidatedException
import android.security.keystore.KeyProperties
import androidx.core.hardware.fingerprint.FingerprintManagerCompat
import java.security.Key
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator

class CryptoObjectHelper {

    private val keyStore: KeyStore = KeyStore.getInstance(KEYSTORE_NAME)
    private var retry = true

    init {
        keyStore.load(null)
    }

    fun getCryptoObject(): FingerprintManagerCompat.CryptoObject {
        return FingerprintManagerCompat.CryptoObject(createCipher())
    }

    private fun createCipher(): Cipher {
        val key = getKey()
        val cipher = Cipher.getInstance(TRANSFORMATION)

        try {
            cipher.init(Cipher.ENCRYPT_MODE, key)
        } catch (e: KeyPermanentlyInvalidatedException) {
            keyStore.deleteEntry(KEY_NAME)
            if (retry) {
                createCipher()
                retry = false
            } else {
                throw Exception("Could not create the cipher for fingerprint authentication")
            }
        }
        return cipher
    }

    private fun getKey(): Key {
        if (keyStore.isKeyEntry(KEY_NAME).not()) {
            createKey()
        }
        return keyStore.getKey(KEY_NAME, null)
    }

    private fun createKey() {
        val keyGenerator = KeyGenerator.getInstance(KEY_ALGORITHM, KEYSTORE_NAME)
        val keyGenParameterSpec = KeyGenParameterSpec.Builder(
            KEY_NAME,
            KeyProperties.PURPOSE_ENCRYPT
        ).setBlockModes(BLOCK_MODE)
            .setEncryptionPaddings(ENCRYPTION_PADDING)
            .setUserAuthenticationRequired(true)
            .build()

        keyGenerator.init(keyGenParameterSpec)
        keyGenerator.generateKey()
    }

    companion object {
        private const val KEY_NAME = "fingerprint_authentication_key"
        private const val KEYSTORE_NAME = "AndroidKeyStore"

        private const val KEY_ALGORITHM = KeyProperties.KEY_ALGORITHM_AES
        private const val BLOCK_MODE = KeyProperties.BLOCK_MODE_CBC
        private const val ENCRYPTION_PADDING = KeyProperties.ENCRYPTION_PADDING_PKCS7
        private const val TRANSFORMATION = "$KEY_ALGORITHM/$BLOCK_MODE/$ENCRYPTION_PADDING"
    }
}
