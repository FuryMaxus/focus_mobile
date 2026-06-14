package com.example.focus.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

// Imports de tus pantallas
import com.example.focus.ui.screen.LoginScreen
import com.example.focus.ui.screen.RegisterScreen
import com.example.focus.ui.screen.HomeScreen
import com.example.focus.ui.screen.ClockScreen
// import com.example.focus.ui.screen.ClockScreen // Descomenta esta línea cuando crees tu pantalla del reloj

@Composable
fun MainScreen() {
    // Esto controla en qué pantalla estamos actualmente
    val navController = rememberNavController()

    // NavHost es el contenedor que cambia las pantallas
    NavHost(navController = navController, startDestination = "menu") {

        // 1. Pantalla del Menú Principal
        composable("menu") {
            MenuScreen(
                onNavigateToLogin = { navController.navigate("login") },
                onNavigateToRegister = { navController.navigate("register") }
            )
        }

        // 2. Pantalla de Iniciar Sesión (¡Ahora recibe el router para poder avanzar al Home!)
        composable("login") {
            LoginScreen(navController = navController)
        }

        // 3. Pantalla de Registro
        composable("register") {
            RegisterScreen(navController = navController)
        }

        // 4. NUEVA: Pantalla Principal (Dashboard/Home)
        composable("home") {
            HomeScreen(navController = navController)
        }

        // 5. NUEVA: Pantalla del Temporizador
        composable("clock") {
            ClockScreen(navController = navController)
            // Aquí llamarás a tu ClockScreen cuando la tengas lista
            // ClockScreen(navController = navController)
        }
    }
}

// El diseño visual de tu Menú de Inicio
@Composable
fun MenuScreen(onNavigateToLogin: () -> Unit, onNavigateToRegister: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Bienvenido a Focus", style = MaterialTheme.typography.headlineLarge)

        Spacer(modifier = Modifier.height(48.dp))

        // Botón para ir al Login
        Button(
            onClick = onNavigateToLogin,
            modifier = Modifier.fillMaxWidth(0.7f) // Ocupa el 70% del ancho de la pantalla
        ) {
            Text("Iniciar Sesión")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón para ir al Registro
        OutlinedButton(
            onClick = onNavigateToRegister,
            modifier = Modifier.fillMaxWidth(0.7f)
        ) {
            Text("Crear Cuenta")
        }
    }
}