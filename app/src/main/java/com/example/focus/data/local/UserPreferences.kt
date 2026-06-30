package com.example.focus.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
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
    private val EQUIPPED_ROOM_ID = stringPreferencesKey("equipped_room_id")
    private val EQUIPPED_ROOM_MULTIPLIER = floatPreferencesKey("equipped_room_multiplier")
    private val USER_ROLE = stringPreferencesKey("user_role")

    val getToken: Flow<String?> = dataStore.data.map { it[TOKEN_KEY] }
    val getLevel: Flow<Int> = dataStore.data.map { it[LEVEL_KEY] ?: 1 }
    val getExp: Flow<Int> = dataStore.data.map { it[TOTAL_EXP_KEY] ?: 0 }

    val getEquippedRoomId: Flow<String?> = dataStore.data.map { preferences ->
        preferences[EQUIPPED_ROOM_ID]
    }

    val getEquippedRoomMultiplier: Flow<Float> = dataStore.data.map { preferences ->
        preferences[EQUIPPED_ROOM_MULTIPLIER] ?: 1.0f
    }

    val getUserRole: Flow<String?> = dataStore.data.map { it[USER_ROLE] }

    var cachedToken: String? = null
        private set

    suspend fun saveToken(token: String) {
        dataStore.edit { it[TOKEN_KEY] = token }
        cachedToken = token
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

    suspend fun equipRoom(roomId: String, multiplier: Float) {
        dataStore.edit { preferences ->
            preferences[EQUIPPED_ROOM_ID] = roomId
            preferences[EQUIPPED_ROOM_MULTIPLIER] = multiplier
        }
    }

    suspend fun unequipRoom() {
        dataStore.edit { preferences ->
            preferences.remove(EQUIPPED_ROOM_ID)
            preferences.remove(EQUIPPED_ROOM_MULTIPLIER)
        }
    }

    suspend fun saveRole(role: String) {
        dataStore.edit { it[USER_ROLE] = role }
    }
}