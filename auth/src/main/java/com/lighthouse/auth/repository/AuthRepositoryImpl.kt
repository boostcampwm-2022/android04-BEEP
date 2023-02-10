package com.lighthouse.auth.repository

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.lighthouse.beep.model.auth.EncryptData
import com.lighthouse.repository.user.AuthRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.security.Key
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.inject.Inject

internal class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : AuthRepository {

    private val keyStore by lazy {
        KeyStore.getInstance(KEYSTORE_NAME).apply {
            load(null)
        }
    }

    private val isGuestFlow = callbackFlow {
        val authStateListener = AuthStateListener {
            trySend(it.currentUser == null)
        }

        awaitClose {
            firebaseAuth.removeAuthStateListener(authStateListener)
        }
    }

    override fun isGuest(): Flow<Boolean> {
        return isGuestFlow
    }

    override fun getCurrentUserId(): String {
        return firebaseAuth.currentUser?.uid ?: GUEST_ID
    }

    override fun encrypt(alias: String, data: String): Result<EncryptData> = runCatching {
        val secretKey = getSecretKey(alias)
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        EncryptData(
            cipher.doFinal(data.toByteArray()),
            cipher.iv
        )
    }

    override fun decrypt(alias: String, data: EncryptData): Result<String> = runCatching {
        val secretKey = getSecretKey(alias)
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, IvParameterSpec(data.iv))
        String(cipher.doFinal(data.data))
    }

    private fun getSecretKey(alias: String): Key {
        return if (keyStore.isKeyEntry(alias).not()) {
            generateKey(alias)
        } else {
            keyStore.getKey(alias, null)
        }
    }

    private fun generateKey(alias: String): SecretKey {
        val keyGenerator = KeyGenerator.getInstance(KEY_ALGORITHM)
        val keyGenParameterSpec = KeyGenParameterSpec.Builder(
            alias,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        ).setBlockModes(BLOCK_MODE)
            .setEncryptionPaddings(ENCRYPTION_PADDINGS)
            .setDigests(KeyProperties.DIGEST_SHA256)
            .setUserAuthenticationRequired(false)
            .build()
        keyGenerator.init(keyGenParameterSpec)
        return keyGenerator.generateKey()
    }

    companion object {
        private const val GUEST_ID = "Guest"

        private const val KEYSTORE_NAME = "AndroidKeyStore"

        private const val KEY_ALGORITHM = KeyProperties.KEY_ALGORITHM_AES
        private const val BLOCK_MODE = KeyProperties.BLOCK_MODE_CBC
        private const val ENCRYPTION_PADDINGS = KeyProperties.ENCRYPTION_PADDING_PKCS7
        private const val TRANSFORMATION = "$KEY_ALGORITHM/$BLOCK_MODE/$ENCRYPTION_PADDINGS"
    }
}
