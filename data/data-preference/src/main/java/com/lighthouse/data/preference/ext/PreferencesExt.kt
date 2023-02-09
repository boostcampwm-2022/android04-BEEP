@file:Suppress("unused")

package com.lighthouse.data.preference.ext

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.byteArrayPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey

private fun createKey(userId: String, key: String): String {
    return "${userId}_$key"
}

internal fun intKey(userId: String, key: String): Preferences.Key<Int> {
    return intPreferencesKey(createKey(userId, key))
}

internal fun doubleKey(userId: String, key: String): Preferences.Key<Double> {
    return doublePreferencesKey(createKey(userId, key))
}

internal fun stringKey(userId: String, key: String): Preferences.Key<String> {
    return stringPreferencesKey(createKey(userId, key))
}

internal fun booleanKey(userId: String, key: String): Preferences.Key<Boolean> {
    return booleanPreferencesKey(createKey(userId, key))
}

internal fun floatKey(userId: String, key: String): Preferences.Key<Float> {
    return floatPreferencesKey(createKey(userId, key))
}

internal fun longKey(userId: String, key: String): Preferences.Key<Long> {
    return longPreferencesKey(createKey(userId, key))
}

internal fun stringSetKey(userId: String, key: String): Preferences.Key<Set<String>> {
    return stringSetPreferencesKey(createKey(userId, key))
}

internal fun byteArrayKey(userId: String, key: String): Preferences.Key<ByteArray> {
    return byteArrayPreferencesKey(createKey(userId, key))
}
