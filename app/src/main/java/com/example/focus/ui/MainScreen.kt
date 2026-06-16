package com.example.focus.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

// Imports de navegación y vistas de Jose
import com.example.focus.navigation.AppRoute
import com.example.focus.navigation.NavigationEvent
import com.example.focus.viewmodel.MainViewModel

// Imports de tus pantallas
import com.example.focus.ui.screen.LoginScreen
import com.example.focus.ui.screen.RegisterScreen
import com.example.focus.ui.screen.HomeScreen
import com.example.focus.ui.screen.ClockScreen
import com.example.focus.ui.screen.DebugScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val context = LocalContext.current
    val navController = rememberNavController()


    val mainViewModel: MainViewModel = viewModel()


    LaunchedEffect(Unit) {
        mainViewModel.navEvents.collect { event ->
            when (event) {
                is NavigationEvent.NavigateTo -> {
                    navController.navigate(event.destination) {
                        launchSingleTop = event.singleTop
                        restoreState = true
                        event.popUpTo?.let { popTarget ->
                            popUpTo(popTarget) {
                                inclusive = event.inclusive
                                saveState = true
                            }
                        }
                    }
                }
                NavigationEvent.NavigateUp -> navController.navigateUp()
                NavigationEvent.PopBackStack -> navController.popBackStack()
            }
        }
    }


    Scaffold { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = AppRoute.Menu, // Iniciamos en tu Menú usando la clase segura
            modifier = Modifier.padding(innerPadding)
        ) {



            composable<AppRoute.Menu> {
                MenuScreen(

                    onNavigateToLogin = { mainViewModel.navigateTo(AppRoute.Login) },
                    onNavigateToRegister = { mainViewModel.navigateTo(AppRoute.Register) }
                )
            }

            composable<AppRoute.Login> {

                LoginScreen(navController = navController)
            }

            composable<AppRoute.Register> {
                RegisterScreen(navController = navController)
            }

            composable<AppRoute.Home> {
                HomeScreen(navController = navController)
            }

            // --- LAS PANTALLAS ORIGINALES ---

            composable<AppRoute.Debug> {
                DebugScreen(onTimerButtonClick = { mainViewModel.navigateTo(AppRoute.Clock) })
            }

            composable<AppRoute.Clock> {
                ClockScreen(navController = navController) // Ajusta esto según si tu ClockScreen pide navController o no
            }
        }
    }
}


@Composable
fun MenuScreen(onNavigateToLogin: () -> Unit, onNavigateToRegister: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Bienvenido a Focus", style = MaterialTheme.typography.headlineLarge)

        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = onNavigateToLogin,
            modifier = Modifier.fillMaxWidth(0.7f)
        ) {
            Text("Iniciar Sesión")
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = onNavigateToRegister,
            modifier = Modifier.fillMaxWidth(0.7f)
        ) {
            Text("Crear Cuenta")
        }
    }
}