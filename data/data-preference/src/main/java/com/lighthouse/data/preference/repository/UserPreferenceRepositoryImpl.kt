package com.lighthouse.data.preference.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
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

    override suspend fun getIV(userId: String): Result<ByteArray?> = runCatchingPref {
        val key = byteArrayKey(userId, KEY_NAME_IV)
        dataStore.data.first()[key]
    }

    override suspend fun setIV(
        userId: String,
        iv: ByteArray
    ): Result<Unit> = runCatchingPref {
        val key = byteArrayKey(userId, KEY_NAME_IV)
        dataStore.edit { pref ->
            pref[key] = iv
        }
    }

    override suspend fun setPinPassword(
        userId: String,
        pinPassword: ByteArray
    ): Result<Unit> = runCatchingPref {
        val key = byteArrayKey(userId, KEY_NAME_PIN_PASSWORD)
        dataStore.edit { pref ->
            pref[key] = pinPassword
        }
    }

    override suspend fun confirmPinPassword(
        userId: String,
        pinPassword: ByteArray
    ): Result<Boolean> = runCatchingPref {
        val key = byteArrayKey(userId, KEY_NAME_PIN_PASSWORD)
        val savedPinPassword = dataStore.data.first()[key]
        savedPinPassword.contentEquals(pinPassword)
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
    ): Result<Flow<SecurityOption>> = runCatchingPref {
        val key = stringKey(userId, KEY_NAME_SECURITY_OPTION)
        dataStore.data.map { pref ->
            val value = pref[key] ?: throw PrefNotFoundException("Security 값이 없습니다.")
            SecurityOption.valueOf(value)
        }
    }

    override suspend fun setNotificationEnable(
        userId: String,
        enable: Boolean
    ): Result<Unit> = runCatchingPref {
        val key = booleanKey(userId, KEY_NAME_NOTIFICATION_ENABLE)
        dataStore.edit { pref ->
            pref[key] = enable
        }
    }

    override fun getNotificationEnable(
        userId: String
    ): Result<Flow<Boolean>> = runCatchingPref {
        val key = booleanKey(userId, KEY_NAME_NOTIFICATION_ENABLE)
        dataStore.data.map { pref ->
            pref[key] ?: throw PrefNotFoundException("Notification Enable 값이 없습니다.")
        }
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

        dataStore.edit { pref ->
            if (iv != null) {
                pref[byteArrayKey(newUserId, KEY_NAME_IV)] = iv
            }
            if (pin != null) {
                pref[byteArrayKey(newUserId, KEY_NAME_PIN_PASSWORD)] = pin
            }
            pref[stringKey(newUserId, KEY_NAME_SECURITY_OPTION)] = securityOption.name
            pref[booleanKey(newUserId, KEY_NAME_NOTIFICATION_ENABLE)] = notificationEnable ?: false
        }

        clearData(oldUserId)
    }

    override suspend fun clearData(userId: String): Result<Unit> = runCatchingPref {
        dataStore.edit { pref ->
            pref.remove(byteArrayKey(userId, KEY_NAME_IV))
            pref.remove(byteArrayKey(userId, KEY_NAME_PIN_PASSWORD))
            pref.remove(byteArrayKey(userId, KEY_NAME_SECURITY_OPTION))
            pref.remove(byteArrayKey(userId, KEY_NAME_NOTIFICATION_ENABLE))
        }
    }

    companion object {
        private const val KEY_NAME_PIN_PASSWORD = "PinPassword"
        private const val KEY_NAME_IV = "IV"
        private const val KEY_NAME_SECURITY_OPTION = "SecurityOption"
        private const val KEY_NAME_NOTIFICATION_ENABLE = "NotificationEnable"
    }
}
