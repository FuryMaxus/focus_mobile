package com.example.focus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.focus.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _confirmPassword = MutableStateFlow("")
    val confirmPassword: StateFlow<String> = _confirmPassword.asStateFlow()

    // true solo cuando ambos campos tienen contenido Y no coinciden.
    // Así no se muestra error mientras el usuario todavía no ha escrito el segundo campo.
    private val _passwordMismatch = MutableStateFlow(false)
    val passwordMismatch: StateFlow<Boolean> = _passwordMismatch.asStateFlow()

    private val _role = MutableStateFlow("student") // Default role
    val role: StateFlow<String> = _role.asStateFlow()

    private val _mensaje = MutableStateFlow("")
    val mensaje: StateFlow<String> = _mensaje.asStateFlow()

    private val _isError = MutableStateFlow(false)
    val isError: StateFlow<Boolean> = _isError.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun onEmailChange(newEmail: String) { _email.value = newEmail }

    fun onPasswordChange(newPassword: String) {
        _password.value = newPassword
        checkPasswordsMatch()
    }

    fun onConfirmPasswordChange(newConfirmPassword: String) {
        _confirmPassword.value = newConfirmPassword
        checkPasswordsMatch()
    }

    private fun checkPasswordsMatch() {
        _passwordMismatch.value = _confirmPassword.value.isNotEmpty() &&
                _password.value != _confirmPassword.value
    }

    fun onRoleChange(newRole: String) { _role.value = newRole }

    fun register(onSuccess: () -> Unit) {
        if (_email.value.isBlank() || _password.value.isBlank() || _confirmPassword.value.isBlank()) {
            _isError.value = true
            _mensaje.value = "Los campos no pueden estar vacíos"
            return
        }

        if (_password.value != _confirmPassword.value) {
            _isError.value = true
            _mensaje.value = "Las contraseñas no coinciden"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _isError.value = false
            _mensaje.value = ""

            authRepository.register(
                email = _email.value.trim(),
                password = _password.value.trim(),
                role = _role.value
            ).fold(
                onSuccess = {
                    _isError.value = false
                    _mensaje.value = "¡Registro exitoso! Redirigiendo al gremio..."
                    // Delay para que el usuario alcance a leer el mensaje antes de navegar.
                    delay(1800)
                    onSuccess()
                },
                onFailure = { exception ->
                    _isError.value = true
                    _mensaje.value = mapRegisterError(exception)
                }
            )
            _isLoading.value = false
        }
    }

    /**
     * Traduce errores técnicos a mensajes legibles.
     * IMPORTANTE: asume que AuthRepository deja pasar retrofit2.HttpException tal cual.
     * Si tu AuthRepository envuelve el error en una excepción propia (ej. ApiException),
     * ajusta el "as? HttpException" de abajo por tu clase real, o comparte AuthRepository.kt
     * para que te lo deje exacto.
     */
    private fun mapRegisterError(exception: Throwable): String {
        val httpCode = (exception as? HttpException)?.code()
            ?: Regex("""\b(\d{3})\b""").find(exception.message ?: "")?.groupValues?.get(1)?.toIntOrNull()

        return when (httpCode) {
            409 -> "Esta cuenta ya existe. Intenta iniciar sesión."
            400 -> "Datos inválidos. Revisa el correo y la contraseña."
            else -> "Error al registrar: ${exception.message ?: "intenta de nuevo"}"
        }
    }
}