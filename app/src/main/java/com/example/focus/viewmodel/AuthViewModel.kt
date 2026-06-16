package com.example.focus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.focus.network.LoginPayload
import com.example.focus.network.RegisterPayload
import com.example.focus.network.RetrofitClient
import com.example.focus.network.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(private val tokenManager: TokenManager) : ViewModel() {

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _mensaje = MutableStateFlow("")
    val mensaje: StateFlow<String> = _mensaje.asStateFlow()

    private val _isError = MutableStateFlow(false)
    val isError: StateFlow<Boolean> = _isError.asStateFlow()

    // Controla si hay una petición en curso
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun onEmailChange(newEmail: String) { _email.value = newEmail }
    fun onPasswordChange(newPassword: String) { _password.value = newPassword }

    fun login(onSuccess: () -> Unit) {
        if (_email.value.isBlank() || _password.value.isBlank()) {
            _isError.value = true
            _mensaje.value = "Los campos no pueden estar vacíos"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val payload = LoginPayload(email = _email.value.trim(), password = _password.value.trim())
                val response = RetrofitClient.apiService.login(payload)

                if (response.access_token.isNotEmpty()) {
                    tokenManager.saveToken(response.access_token)
                    _isError.value = false
                    _mensaje.value = ""
                    onSuccess()
                }
            } catch (e: Exception) {
                _isError.value = true
                _mensaje.value = "Error al iniciar sesión: HTTP ${e.message}" // Extraemos el código de error para diagnosticar mejor
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun register(onSuccess: () -> Unit) {
        if (_email.value.isBlank() || _password.value.isBlank()) {
            _isError.value = true
            _mensaje.value = "Los campos no pueden estar vacíos"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Aquí usamos tu código original sin trim()
                val payload = RegisterPayload(email = _email.value, password = _password.value)
                val response = RetrofitClient.apiService.register(payload)

                _isError.value = false
                _mensaje.value = "¡Registro exitoso! ID: ${response.id}"
                onSuccess()
            } catch (e: Exception) {
                _isError.value = true
                _mensaje.value = "Error al registrar: HTTP ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}

class AuthViewModelFactory(private val tokenManager: TokenManager) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(tokenManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}