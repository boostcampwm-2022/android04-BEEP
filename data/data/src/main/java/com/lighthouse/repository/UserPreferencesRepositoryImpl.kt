package com.lighthouse.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.byteArrayPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.lighthouse.beep.model.user.UserPreferenceOption
import com.lighthouse.core.android.utils.crypto.CryptoObjectHelper
import com.lighthouse.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UserPreferencesRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : UserPreferencesRepository {
    private var uid = Firebase.auth.currentUser?.uid ?: "Guest"
    private val guestKey = booleanPreferencesKey("guest")
    private val pinKey get() = byteArrayPreferencesKey("${uid}pin")
    private val ivKey get() = byteArrayPreferencesKey("${uid}iv")
    private val securityKey get() = intPreferencesKey("${uid}security")
    private val notificationKey get() = booleanPreferencesKey("${uid}notification")

    override fun isStored(option: UserPreferenceOption): Flow<Boolean> {
        val key = when (option) {
            UserPreferenceOption.SECURITY -> securityKey
            UserPreferenceOption.NOTIFICATION -> notificationKey
            UserPreferenceOption.GUEST -> guestKey
        }

        return dataStore.data.map { preferences ->
            preferences.contains(key)
        }
    }

    override suspend fun moveGuestData(uid: String): Result<Unit> = runCatching {
        val pin = dataStore.data.map { it[pinKey] }.first()
        val iv = dataStore.data.map { it[ivKey] }.first()
        val security = dataStore.data.map { it[securityKey] }.first()
        val notification = dataStore.data.map { it[notificationKey] }.first()

        this.uid = uid

        withContext(Dispatchers.IO) {
            dataStore.edit { preferences ->
                preferences[pinKey] = pin as ByteArray
                preferences[ivKey] = iv as ByteArray
                preferences[securityKey] = security as Int
                preferences[notificationKey] = notification as Boolean
            }
        }
    }

    override suspend fun removeCurrentUserData(): Result<Unit> = runCatching {
        dataStore.edit { preferences ->
            preferences.remove(guestKey)
            if (preferences.contains(pinKey)) {
                preferences.remove(pinKey)
                preferences.remove(ivKey)
            }
            preferences.remove(securityKey)
            preferences.remove(notificationKey)
        }
    }

    override suspend fun setPinString(pinString: String): Result<Unit> = runCatching {
        withContext(Dispatchers.IO) {
            dataStore.edit { preferences ->
                val cryptoResult = CryptoObjectHelper.encrypt(pinString)
                preferences[pinKey] = cryptoResult.first
                preferences[ivKey] = cryptoResult.second
            }
        }
    }

    override fun getPinString(): Flow<String> = dataStore.data.map { preferences ->
        val pin = preferences[pinKey] ?: throw Exception("Cannot Find Encrypted PIN")
        val iv = preferences[ivKey] ?: throw Exception("Cannot Find IV")

        CryptoObjectHelper.decrypt(pin, iv)
    }

    override suspend fun setSecurityOption(value: Int): Result<Unit> = runCatching {
        withContext(Dispatchers.IO) {
            dataStore.edit { preferences ->
                preferences[securityKey] = value
            }
        }
    }

    override fun getSecurityOption(): Flow<Int> {
        return dataStore.data.map { preferences ->
            preferences[securityKey] ?: 0
        }
    }

    override suspend fun setBooleanOption(
        option: UserPreferenceOption,
        value: Boolean
    ): Result<Unit> = runCatching {
        val key = getPreferenceKey(option)

        withContext(Dispatchers.IO) {
            dataStore.edit { preferences ->
                preferences[key] = value
            }
        }
    }

    override fun getBooleanOption(option: UserPreferenceOption): Flow<Boolean> {
        val key = getPreferenceKey(option)

        return dataStore.data.map { preferences ->
            preferences[key] ?: false
        }
    }

    private fun getPreferenceKey(option: UserPreferenceOption): Preferences.Key<Boolean> {
        return when (option) {
            UserPreferenceOption.GUEST -> guestKey
            UserPreferenceOption.NOTIFICATION -> notificationKey
            else -> throw Exception("Unsupportable Option : Not Boolean")
        }
    }
}
