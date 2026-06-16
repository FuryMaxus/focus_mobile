package com.example.focus.network

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Inicializa el DataStore
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "focus_settings")

class TokenManager(private val context: Context) {

    companion object {
        val JWT_TOKEN_KEY = stringPreferencesKey("jwt_token")
        val LEVEL_KEY = intPreferencesKey("user_level")
        val EXP_KEY = intPreferencesKey("user_exp")
    }

    // --- TOKEN ---
    suspend fun saveToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[JWT_TOKEN_KEY] = token
        }
    }

    val getToken: Flow<String?> = context.dataStore.data.map { it[JWT_TOKEN_KEY] }

    // --- ESTADÍSTICAS (XP Y NIVEL) ---
    suspend fun saveStats(level: Int, expGained: Int) {
        context.dataStore.edit { preferences ->
            preferences[LEVEL_KEY] = level
            val currentExp = preferences[EXP_KEY] ?: 0
            preferences[EXP_KEY] = currentExp + expGained
        }
    }

    val getLevel: Flow<Int> = context.dataStore.data.map { it[LEVEL_KEY] ?: 1 }
    val getExp: Flow<Int> = context.dataStore.data.map { it[EXP_KEY] ?: 0 }

    // --- CERRAR SESIÓN ---
    suspend fun clearAll() {
        context.dataStore.edit { preferences ->
            preferences.clear() // Borra el token y resetea las estadísticas locales
        }
    }
}