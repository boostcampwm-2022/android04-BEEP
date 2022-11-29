package com.lighthouse.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.byteArrayPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import com.lighthouse.domain.model.UserPreferenceOption
import com.lighthouse.domain.repository.UserPreferencesRepository
import com.lighthouse.util.CryptoObjectHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UserPreferencesRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : UserPreferencesRepository {
    override suspend fun setPinString(pinString: String): Result<Unit> = runCatching {
        withContext(Dispatchers.IO) {
            dataStore.edit { preferences ->
                val cryptoResult = CryptoObjectHelper.encrypt(pinString)
                preferences[PIN] = cryptoResult.first
                preferences[IV] = cryptoResult.second
            }
        }
    }

    override fun getPinString(): Flow<String> = dataStore.data.map { preferences ->
        val pin = preferences[PIN] ?: throw Exception("Cannot Find Encrypted PIN")
        val iv = preferences[IV] ?: throw Exception("Cannot Find IV")

        CryptoObjectHelper.decrypt(pin, iv)
    }

    override suspend fun setIntOption(option: UserPreferenceOption, value: Int): Result<Unit> = runCatching {
        withContext(Dispatchers.IO) {
            val key = when (option) {
                UserPreferenceOption.SECURITY -> SECURITY
                else -> throw Exception("Unsupportable Option : Not Int")
            }

            dataStore.edit { preferences ->
                preferences[key] = value
            }
        }
    }

    override fun getIntOption(option: UserPreferenceOption): Flow<Int> {
        val key = when (option) {
            UserPreferenceOption.SECURITY -> SECURITY
            else -> throw Exception("Unsupportable Option : Not Int")
        }

        return dataStore.data.map { preferences ->
            preferences[key] ?: throw Exception("Cannot Find Int Option")
        }
    }

    override suspend fun setBooleanOption(option: UserPreferenceOption, value: Boolean): Result<Unit> = runCatching {
        withContext(Dispatchers.IO) {
            val key = when (option) {
                UserPreferenceOption.NOTIFICATION -> NOTIFICATION
                UserPreferenceOption.LOCATION -> LOCATION
                else -> throw Exception("Unsupportable Option : Not Boolean")
            }

            dataStore.edit { preferences ->
                preferences[key] = value
            }
        }
    }

    override fun getBooleanOption(option: UserPreferenceOption): Flow<Boolean> {
        val key = when (option) {
            UserPreferenceOption.NOTIFICATION -> NOTIFICATION
            UserPreferenceOption.LOCATION -> LOCATION
            else -> throw Exception("Unsupportable Option : Not Boolean")
        }

        return dataStore.data.map { preferences ->
            preferences[key] ?: throw Exception("Cannot Find Boolean Option")
        }
    }

    companion object {
        val PIN = byteArrayPreferencesKey("pin")
        val IV = byteArrayPreferencesKey("iv")
        val SECURITY = intPreferencesKey("security")
        val NOTIFICATION = booleanPreferencesKey("notification")
        val LOCATION = booleanPreferencesKey("location")
    }
}
