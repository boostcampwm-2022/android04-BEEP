package com.lighthouse.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.byteArrayPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.lighthouse.domain.model.UserPreferenceOption
import com.lighthouse.domain.repository.UserPreferencesRepository
import com.lighthouse.util.CryptoObjectHelper
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
    private val locationKey get() = booleanPreferencesKey("${uid}location")

    override fun isStored(option: UserPreferenceOption): Flow<Boolean> {
        val key = when (option) {
            UserPreferenceOption.SECURITY -> securityKey
            UserPreferenceOption.NOTIFICATION -> notificationKey
            UserPreferenceOption.LOCATION -> locationKey
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
        // val notification = dataStore.data.map { it[notificationKey] }.first()
        // val location = dataStore.data.map { it[locationKey] }.first()

        this.uid = uid

        withContext(Dispatchers.IO) {
            dataStore.edit { preferences ->
                preferences[pinKey] = pin as ByteArray
                preferences[ivKey] = iv as ByteArray
                preferences[securityKey] = security as Int
                // preferences[notificationKey] = notification as Boolean
                // preferences[locationKey] = location as Boolean
                preferences[guestKey] = false
            }
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
            preferences[securityKey] ?: throw Exception("Cannot Find Int Option")
        }
    }

    override suspend fun setBooleanOption(option: UserPreferenceOption, value: Boolean): Result<Unit> = runCatching {
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
            preferences[key] ?: throw Exception("Cannot Find Boolean Option")
        }
    }

    private fun getPreferenceKey(option: UserPreferenceOption): Preferences.Key<Boolean> {
        return when (option) {
            UserPreferenceOption.GUEST -> guestKey
            UserPreferenceOption.NOTIFICATION -> notificationKey
            UserPreferenceOption.LOCATION -> locationKey
            else -> throw Exception("Unsupportable Option : Not Boolean")
        }
    }
}
