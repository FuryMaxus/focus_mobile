package com.example.focus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.focus.network.TokenManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(private val tokenManager: TokenManager) : ViewModel() {


    val nivel: StateFlow<Int> = tokenManager.getLevel
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 1)

    // Exponemos la experiencia actual
    val expActual: StateFlow<Int> = tokenManager.getExp
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Calculamos la experiencia faltante reactivamente
    val expFaltante: StateFlow<Int> = kotlinx.coroutines.flow.combine(nivel, expActual) { niv, exp ->
        (niv * 100) - exp
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Calculamos el progreso (de 0.0 a 1.0) para la barra
    val progreso: StateFlow<Float> = kotlinx.coroutines.flow.combine(nivel, expActual) { niv, exp ->
        val expNecesaria = niv * 100
        if (expNecesaria > 0) exp.toFloat() / expNecesaria.toFloat() else 0f
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0f)

    // Función pura para delegar el cierre de sesión
    fun logout(onLogoutSuccess: () -> Unit) {
        viewModelScope.launch {
            tokenManager.clearAll()
            onLogoutSuccess()
        }
    }
}

// Factory para poder inyectar el TokenManager al ViewModel
class HomeViewModelFactory(private val tokenManager: TokenManager) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(tokenManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}