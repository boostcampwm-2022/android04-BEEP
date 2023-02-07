package com.lighthouse.core.android.utils.crypto

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.Key
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.spec.IvParameterSpec

object CryptoObjectHelper {

    private const val KEY_NAME_FINGERPRINT = "fingerprint_authentication_key"
    private const val KEY_NAME_PIN = "pin_authentication_key"
    private const val KEYSTORE_NAME = "AndroidKeyStore"

    private const val KEY_ALGORITHM = KeyProperties.KEY_ALGORITHM_AES
    private const val BLOCK_MODE = KeyProperties.BLOCK_MODE_CBC
    private const val ENCRYPTION_PADDING = KeyProperties.ENCRYPTION_PADDING_PKCS7
    private const val TRANSFORMATION = "$KEY_ALGORITHM/$BLOCK_MODE/$ENCRYPTION_PADDING"

    private val keyStore: KeyStore = KeyStore.getInstance(KEYSTORE_NAME)

    init {
        keyStore.load(null)
    }

    fun getFingerprintCipher(): Cipher {
        val key = getKey(KEY_NAME_FINGERPRINT)
        return Cipher.getInstance(TRANSFORMATION).also {
            it.init(Cipher.ENCRYPT_MODE, key)
        }
    }

    private fun getKey(keyName: String): Key {
        if (keyStore.isKeyEntry(keyName).not()) {
            createKey(keyName)
        }
        return keyStore.getKey(keyName, null)
    }

    private fun createKey(keyName: String) {
        val keyGenerator = KeyGenerator.getInstance(KEY_ALGORITHM, KEYSTORE_NAME)
        val keyGenParameterSpec = when (keyName) {
            KEY_NAME_FINGERPRINT -> {
                KeyGenParameterSpec.Builder(
                    KEY_NAME_FINGERPRINT,
                    KeyProperties.PURPOSE_ENCRYPT
                ).setBlockModes(BLOCK_MODE)
                    .setEncryptionPaddings(ENCRYPTION_PADDING)
                    .setUserAuthenticationRequired(false)
                    .build()
            }

            KEY_NAME_PIN -> {
                KeyGenParameterSpec.Builder(
                    KEY_NAME_PIN,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                ).setBlockModes(BLOCK_MODE)
                    .setEncryptionPaddings(ENCRYPTION_PADDING)
                    .setDigests(KeyProperties.DIGEST_SHA256)
                    .setUserAuthenticationRequired(false)
                    .build()
            }

            else -> return
        }
        keyGenerator.init(keyGenParameterSpec)
        keyGenerator.generateKey()
    }

    fun encrypt(pin: String): Pair<ByteArray, ByteArray> {
        if (keyStore.containsAlias(KEY_NAME_PIN).not()) {
            createKey(KEY_NAME_PIN)
        }
        val secretKey = getKey(KEY_NAME_PIN)
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)

        val encrypted = cipher.doFinal(pin.toByteArray(Charsets.UTF_8))
        val iv = cipher.iv

        return Pair(encrypted, iv)
    }

    fun decrypt(encrypted: ByteArray, iv: ByteArray): String {
        val secretKey = getKey(KEY_NAME_PIN)
        val cipher = Cipher.getInstance(TRANSFORMATION)

        cipher.init(Cipher.DECRYPT_MODE, secretKey, IvParameterSpec(iv))
        val decrypted = cipher.doFinal(encrypted)
        return String(decrypted, Charsets.UTF_8)
    }
}
