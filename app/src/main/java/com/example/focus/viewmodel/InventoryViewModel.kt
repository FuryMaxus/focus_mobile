package com.example.focus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.focus.data.local.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
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

    fun selectCharacter(name: String) {
        viewModelScope.launch {
            userPreferences.saveCharacter(name)
        }
    }
}
