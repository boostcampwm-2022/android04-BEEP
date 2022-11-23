package com.lighthouse.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.lighthouse.domain.repository.UserPreferencesRepository
import javax.inject.Inject

class UserPreferencesRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : UserPreferencesRepository {
    override fun setPin(pinString: String) {
        TODO("Not yet implemented")
    }

    override fun getCorrespondWithPin(pinString: String) {
        TODO("Not yet implemented")
    }
}
