package com.example.focus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.focus.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _mensaje = MutableStateFlow("")
    val mensaje: StateFlow<String> = _mensaje.asStateFlow()

    private val _isError = MutableStateFlow(false)
    val isError: StateFlow<Boolean> = _isError.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun onEmailChange(newEmail: String) { _email.value = newEmail }
    fun onPasswordChange(newPassword: String) { _password.value = newPassword }

    fun login(onNavigateStudent: () -> Unit, onNavigateDM: () -> Unit) {
        if (_email.value.isBlank() || _password.value.isBlank()) {
            _isError.value = true
            _mensaje.value = "Los campos no pueden estar vacíos"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _isError.value = false
            _mensaje.value = ""

            authRepository.login(
                email=_email
                    .value
                    .trim(),
                password = _password
                    .value
                    .trim()
            ).fold(
                onSuccess = { role ->
                    when (role.lowercase()) {
                        "student" -> onNavigateStudent()
                        "dm" -> onNavigateDM()
                        else -> {
                            _isError.value = true
                            _mensaje.value = "Rol no reconocido: $role"
                        }
                    }
                },
                onFailure = { exception ->
                    _isError.value = true
                    _mensaje.value = "Error al iniciar sesión: ${exception.message}"
                }
            )
            _isLoading.value = false
        }
    }
}


