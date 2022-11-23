package com.lighthouse.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.lighthouse.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserPreferencesRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : UserPreferencesRepository {
    override suspend fun setPin(pinString: String) {
        dataStore.edit { preferences ->
            preferences[PIN] = pinString
        }
    }

    override fun getPin(): Flow<String> = dataStore.data.map { preferences ->
        preferences[PIN] ?: ""
    }

    companion object {
        val PIN = stringPreferencesKey("pin")
    }
}
