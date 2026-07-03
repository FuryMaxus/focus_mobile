package com.example.focus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.focus.data.local.UserPreferences
import com.example.focus.ui.component.GuildHat
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userPreferences: UserPreferences
) : ViewModel() {

    val nivel: StateFlow<Int> = userPreferences.getLevel
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 1
        )

    val character: StateFlow<String> = userPreferences.getCharacter
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = "Duende"
        )
        
    val equippedHat: StateFlow<GuildHat> = userPreferences.getEquippedHat
        .map { GuildHat.fromName(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = GuildHat.Ninguno
        )

    val expActual: StateFlow<Int> = userPreferences.getExp
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    val expFaltante: StateFlow<Int> = combine(nivel, expActual) { niv, exp ->
        val expNecesaria = niv * 100
        val faltante = expNecesaria - exp
        if (faltante > 0) faltante else 0
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    val progreso: StateFlow<Float> = combine(nivel, expActual) { niv, exp ->
        val expNecesaria = niv * 100
        if (expNecesaria > 0) {
            (exp.toFloat() / expNecesaria.toFloat()).coerceIn(0f, 1f)
        } else {
            0f
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0f
    )

    fun logout(onLogoutSuccess: () -> Unit) {
        viewModelScope.launch {
            userPreferences.clearSession()
            onLogoutSuccess()
        }
    }
}

