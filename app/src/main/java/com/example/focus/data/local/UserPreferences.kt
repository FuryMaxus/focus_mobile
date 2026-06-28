package com.example.focus.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


@Singleton
class UserPreferences @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    private val TOKEN_KEY = stringPreferencesKey("jwt_token")
    private val LEVEL_KEY = intPreferencesKey("user_level")
    private val TOTAL_EXP_KEY = intPreferencesKey("toal_user_exp")
    private val CHARACTER_KEY = stringPreferencesKey("selected_character")


    val getToken: Flow<String?> = dataStore.data.map { it[TOKEN_KEY] }
    val getLevel: Flow<Int> = dataStore.data.map { it[LEVEL_KEY] ?: 1 }
    val getExp: Flow<Int> = dataStore.data.map { it[TOTAL_EXP_KEY] ?: 0 }
    val getCharacter: Flow<String> = dataStore.data.map { it[CHARACTER_KEY] ?: "Duende" }

    var cachedToken: String? = null
        private set

    suspend fun saveToken(token: String) {
        dataStore.edit { it[TOKEN_KEY] = token }
        cachedToken = token
    }
    suspend fun saveCharacter(name: String) {
        dataStore.edit { it[CHARACTER_KEY] = name }
    }
    suspend fun saveLevelAndExp(level: Int, exp: Int) {
        dataStore.edit {
            it[LEVEL_KEY] = level
            it[TOTAL_EXP_KEY] = exp
        }
    }
    suspend fun clearSession() {
        dataStore.edit { it.clear() }
        cachedToken = null
    }
}