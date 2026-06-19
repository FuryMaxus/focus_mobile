package com.example.focus.network

import com.example.focus.data.local.UserPreferences
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

sealed class AuthEvent {
    object LoggedOut : AuthEvent()
}

@Singleton
class AuthManager @Inject constructor(
    private val userPreferences: UserPreferences
) {
    private val _authEvent = MutableSharedFlow<AuthEvent>()
    val authEvent = _authEvent.asSharedFlow()

    suspend fun triggerSessionExpired() {
        userPreferences.clearSession()

        _authEvent.emit(AuthEvent.LoggedOut)
    }
}