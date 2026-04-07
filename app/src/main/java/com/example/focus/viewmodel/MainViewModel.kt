package com.example.focus.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.focus.navigation.NavigationEvent
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import io.ktor.client.call.body
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json

@Serializable
data class UserRequest(val email: String, val password: String)

@Serializable
data class LoginResponse(
    val access_token: String,
    val token_type: String,
    val message: String? = null
)

class MainViewModel(private val tokenManager: TokenManager) : ViewModel() {
    // Variable para guardar el token en memoria por ahora
    var userToken: String? = null

    private val _usersList = mutableStateOf<Map<String, String>>(emptyMap())
    val usersList: State<Map<String, String>> = _usersList

    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
    }

    private val _navEvents = Channel<NavigationEvent>()
    val navEvents = _navEvents.receiveAsFlow()

    // Dirección del backend de Litestar
    private val BASE_URL = "http://10.0.2.2:8000"

    init {
        checkSession()
    }

    private fun checkSession() {
        viewModelScope.launch {
            val savedToken = tokenManager.getToken.first()
            if (!savedToken.isNullOrEmpty()) {
                userToken = savedToken
                Log.d("DEBUG_FOCUS", "Sesión recuperada. Entrando al Home.")
                navigateTo(com.example.focus.navigation.AppRoute.Home, popUpTo = com.example.focus.navigation.AppRoute.Debug, inclusive = true)
            } else {
                Log.d("DEBUG_FOCUS", "No hay sesión activa.")
            }
        }
    }

    fun login(email: String, psw: String) {
        viewModelScope.launch {
            try {
                val response = client.post("$BASE_URL/login") {
                    contentType(ContentType.Application.Json)
                    setBody(UserRequest(email, psw))
                }
                
                if (response.status.value in 200..299) {
                    val loginData: LoginResponse = response.body()
                    userToken = loginData.access_token
                    
                    // Guardar el token de forma persistente
                    viewModelScope.launch {
                        tokenManager.saveToken(loginData.access_token)
                    }
                    
                    println("Login Exitoso! Token guardado.")
                    navigateTo(com.example.focus.navigation.AppRoute.Home)
                } else {
                    println("Error en Login: ${response.status}")
                }
            } catch (e: Exception) {
                println("Error de red: ${e.message}")
            }
        }
    }

    fun register(email: String, psw: String) {
        viewModelScope.launch {
            try {
                val response = client.post("$BASE_URL/register") {
                    contentType(ContentType.Application.Json)
                    setBody(UserRequest(email, psw))
                }
                println("Respuesta Registro: ${response.status}")
            } catch (e: Exception) {
                println("Error en registro: ${e.message}")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            tokenManager.clearToken()
            userToken = null
            println("Sesión cerrada")
            // Volver al menú de debug o login
            navigateTo(com.example.focus.navigation.AppRoute.Debug)
        }
    }

    fun fetchUsers() {
        viewModelScope.launch {
            try {
                println("Solicitando usuarios a: $BASE_URL/users")
                val response = client.get("$BASE_URL/users")
                println("Status del servidor: ${response.status}")
                
                if (response.status.value == 200) {
                    // Intentamos leerlo como un mapa genérico primero
                    val bodyText: String = response.body()
                    println("Cuerpo de respuesta: $bodyText")
                    
                    val data: Map<String, Map<String, String>> = Json.decodeFromString(bodyText)
                    _usersList.value = data["users"] ?: emptyMap()
                    println("Usuarios cargados: ${_usersList.value.size}")
                }
            } catch (e: Exception) {
                println("Error CRÍTICO obteniendo usuarios: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    fun navigateTo(
        destination: Any,
        popUpTo: Any? = null,
        inclusive: Boolean = false,
        singleTop: Boolean = false
    ) {
        viewModelScope.launch {
            _navEvents.send(
                NavigationEvent.NavigateTo(
                    destination = destination,
                    popUpTo = popUpTo,
                    inclusive = inclusive,
                    singleTop = singleTop
                )
            )
        }
    }

    fun navigateBack() {
        viewModelScope.launch {
            _navEvents.send(NavigationEvent.PopBackStack)
        }
    }

    fun navigateUp() {
        viewModelScope.launch {
            _navEvents.send(NavigationEvent.NavigateUp)
        }
    }
}