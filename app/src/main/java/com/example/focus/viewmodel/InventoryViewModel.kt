package com.example.focus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.focus.data.local.UserPreferences
import com.example.focus.ui.component.GuildHat
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InventoryViewModel @Inject constructor(
    private val userPreferences: UserPreferences
) : ViewModel() {

    /** Nombre del personaje equipado (coincide con GuildCharacter.name). */
    val selectedCharacter: StateFlow<String> = userPreferences.getCharacter
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

    // Lista de gorros obtenidos (simulada por ahora, conectada a la estructura de la DB)
    private val _ownedHats = MutableStateFlow<List<GuildHat>>(listOf(GuildHat.Mago))
    val ownedHats = _ownedHats.asStateFlow()

    fun selectCharacter(name: String) {
        viewModelScope.launch {
            userPreferences.saveCharacter(name)
        }
    }

    fun equipHat(hat: GuildHat) {
        viewModelScope.launch {
            userPreferences.saveHat(hat.name)
        }
    }
}
