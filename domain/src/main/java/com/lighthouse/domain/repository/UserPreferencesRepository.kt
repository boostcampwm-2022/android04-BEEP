package com.lighthouse.domain.repository

interface UserPreferencesRepository {
    fun setPin(pinString: String)
    fun getCorrespondWithPin(pinString: String)
}
