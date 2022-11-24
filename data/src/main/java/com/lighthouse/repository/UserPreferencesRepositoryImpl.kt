package com.lighthouse.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.byteArrayPreferencesKey
import androidx.datastore.preferences.core.edit
import com.lighthouse.domain.repository.UserPreferencesRepository
import com.lighthouse.util.CryptoObjectHelper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserPreferencesRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : UserPreferencesRepository {
    override suspend fun setPinString(pinString: String): Result<Unit> = runCatching {
        dataStore.edit { preferences ->
            val cryptoResult = CryptoObjectHelper.encrypt(pinString)
            preferences[PIN] = cryptoResult.first
            preferences[IV] = cryptoResult.second
        }
        Unit
    }

    override fun getPinString(): Flow<String> = dataStore.data.map { preferences ->
        preferences[PIN]?.let { pin ->
            preferences[IV]?.let { iv -> CryptoObjectHelper.decrypt(pin, iv) }
        } ?: ""
    }

    companion object {
        val PIN = byteArrayPreferencesKey("pin")
        val IV = byteArrayPreferencesKey("iv")
    }
}
