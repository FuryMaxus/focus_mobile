package com.example.focus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.focus.data.local.UserPreferences
import com.example.focus.ui.utils.MusicManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPreferences: UserPreferences,
    private val musicManager: MusicManager
) : ViewModel() {

    val isMusicEnabled: StateFlow<Boolean> = userPreferences.isMusicEnabled
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = true
        )

    fun toggleMusic(enabled: Boolean) {
        viewModelScope.launch {
            userPreferences.saveMusicEnabled(enabled)
            if (enabled) {
                musicManager.startMusic()
            } else {
                musicManager.stopMusic()
            }
        }
    }
}
