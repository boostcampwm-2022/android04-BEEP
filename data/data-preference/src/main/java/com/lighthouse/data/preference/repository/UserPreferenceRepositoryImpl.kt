package com.lighthouse.data.preference.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.lighthouse.beep.model.auth.EncryptData
import com.lighthouse.beep.model.user.SecurityOption
import com.lighthouse.data.preference.exception.PrefNotFoundException
import com.lighthouse.data.preference.ext.booleanKey
import com.lighthouse.data.preference.ext.byteArrayKey
import com.lighthouse.data.preference.ext.runCatchingPref
import com.lighthouse.data.preference.ext.stringKey
import com.lighthouse.repository.user.UserPreferenceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class UserPreferenceRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : UserPreferenceRepository {

    override suspend fun setEncryptData(
        userId: String,
        encryptData: EncryptData
    ): Result<Unit> = runCatchingPref {
        val pinPasswordKey = byteArrayKey(userId, KEY_NAME_PIN_PASSWORD)
        val ivKey = byteArrayKey(userId, KEY_NAME_IV)

        dataStore.edit { pref ->
            pref[pinPasswordKey] = encryptData.data
            pref[ivKey] = encryptData.iv
        }
    }

    override suspend fun getEncryptData(userId: String): Result<EncryptData> = runCatchingPref {
        val pinPasswordKey = byteArrayKey(userId, KEY_NAME_PIN_PASSWORD)
        val ivKey = byteArrayKey(userId, KEY_NAME_IV)

        dataStore.data.map { pref ->
            val data = pref[pinPasswordKey]
                ?: throw PrefNotFoundException("PinPassword 값이 없습니다.")
            val iv = pref[ivKey] ?: throw PrefNotFoundException("IV 값이 없습니다.")
            EncryptData(data, iv)
        }.first()
    }

    override suspend fun setSecurityOption(
        userId: String,
        securityOption: SecurityOption
    ): Result<Unit> = runCatchingPref {
        val key = stringKey(userId, KEY_NAME_SECURITY_OPTION)
        dataStore.edit { pref ->
            pref[key] = securityOption.name
        }
    }

    override fun getSecurityOption(
        userId: String
    ): Flow<Result<SecurityOption>> {
        val key = stringKey(userId, KEY_NAME_SECURITY_OPTION)
        return dataStore.data.map { pref ->
            runCatchingPref {
                val value = pref[key] ?: throw PrefNotFoundException("Security 값이 없습니다.")
                SecurityOption.valueOf(value)
            }
        }
    }

    override suspend fun setNotificationEnable(
        userId: String,
        enable: Boolean
    ): Result<Unit> = runCatchingPref {
        return setBoolean(userId, KEY_NAME_NOTIFICATION_ENABLE, enable)
    }

    override fun getNotificationEnable(
        userId: String
    ): Flow<Result<Boolean>> {
        return getBoolean(userId, KEY_NAME_NOTIFICATION_ENABLE)
    }

    override suspend fun setFilterExpired(
        userId: String,
        filterExpired: Boolean
    ): Result<Unit> {
        return setBoolean(userId, KEY_NAME_FILTER_EXPIRED, filterExpired)
    }

    override fun getFilterExpired(userId: String): Flow<Result<Boolean>> {
        return getBoolean(userId, KEY_NAME_FILTER_EXPIRED)
    }

    override suspend fun transferData(
        oldUserId: String,
        newUserId: String
    ): Result<Unit> = runCatchingPref {
        val data = dataStore.data.first()
        val iv = data[byteArrayKey(oldUserId, KEY_NAME_IV)]
        val pin = data[byteArrayKey(oldUserId, KEY_NAME_PIN_PASSWORD)]
        val securityOption = data[stringKey(oldUserId, KEY_NAME_SECURITY_OPTION)]?.let {
            SecurityOption.valueOf(it)
        } ?: throw PrefNotFoundException("Security 값이 없습니다.")
        val notificationEnable = data[booleanKey(oldUserId, KEY_NAME_NOTIFICATION_ENABLE)]
        val filterExpired = data[booleanKey(oldUserId, KEY_NAME_FILTER_EXPIRED)]

        dataStore.edit { pref ->
            if (iv != null) {
                pref[byteArrayKey(newUserId, KEY_NAME_IV)] = iv
            }
            if (pin != null) {
                pref[byteArrayKey(newUserId, KEY_NAME_PIN_PASSWORD)] = pin
            }
            pref[stringKey(newUserId, KEY_NAME_SECURITY_OPTION)] = securityOption.name
            pref[booleanKey(newUserId, KEY_NAME_NOTIFICATION_ENABLE)] = notificationEnable ?: false
            pref[booleanKey(newUserId, KEY_NAME_FILTER_EXPIRED)] = filterExpired ?: false
        }

        clearData(oldUserId)
    }

    override suspend fun clearData(userId: String): Result<Unit> = runCatchingPref {
        dataStore.edit { pref ->
            pref.remove(byteArrayKey(userId, KEY_NAME_IV))
            pref.remove(byteArrayKey(userId, KEY_NAME_PIN_PASSWORD))
            pref.remove(byteArrayKey(userId, KEY_NAME_SECURITY_OPTION))
            pref.remove(byteArrayKey(userId, KEY_NAME_NOTIFICATION_ENABLE))
            pref.remove(byteArrayKey(userId, KEY_NAME_FILTER_EXPIRED))
        }
    }

    private suspend fun setBoolean(
        userId: String,
        keyName: String,
        value: Boolean
    ): Result<Unit> = runCatchingPref {
        val key = booleanKey(userId, keyName)
        dataStore.edit { pref ->
            pref[key] = value
        }
    }

    private fun getBoolean(userId: String, keyName: String): Flow<Result<Boolean>> {
        val key = booleanKey(userId, keyName)
        return dataStore.data.map { pref ->
            runCatchingPref {
                pref[key] ?: throw PrefNotFoundException("$keyName 값이 없습니다.")
            }
        }
    }

    companion object {
        private const val KEY_NAME_PIN_PASSWORD = "PinPassword"
        private const val KEY_NAME_IV = "IV"
        private const val KEY_NAME_SECURITY_OPTION = "SecurityOption"
        private const val KEY_NAME_NOTIFICATION_ENABLE = "NotificationEnable"
        private const val KEY_NAME_FILTER_EXPIRED = "FilterExpired"
    }
}
