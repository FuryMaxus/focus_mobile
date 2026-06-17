package com.example.focus.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch


@Singleton
class UserPreferences @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    private val TOKEN_KEY = stringPreferencesKey("jwt_token")
    private val LEVEL_KEY = intPreferencesKey("user_level")
    private val EXP_KEY = intPreferencesKey("user_exp")

    val getToken: Flow<String?> = dataStore.data.map { it[TOKEN_KEY] }
    val getLevel: Flow<Int> = dataStore.data.map { it[LEVEL_KEY] ?: 1 }
    val getExp: Flow<Int> = dataStore.data.map { it[EXP_KEY] ?: 0 }

    var cachedToken: String? = null
        private set

    suspend fun saveToken(token: String) {
        dataStore.edit { it[TOKEN_KEY] = token }
        cachedToken = token
    }
    suspend fun saveLevelAndExp(level: Int, exp: Int) {
        dataStore.edit {
            it[LEVEL_KEY] = level
            it[EXP_KEY] = exp
        }
    }
    suspend fun clearSession() {
        dataStore.edit { it.clear() }
        cachedToken = null
    }
}