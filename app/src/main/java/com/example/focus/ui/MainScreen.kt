package com.example.focus.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.focus.navigation.AppRoute
import com.example.focus.navigation.NavigationEvent
import com.example.focus.viewmodel.MainViewModel
import com.example.focus.ui.screen.LoginScreen
import com.example.focus.ui.screen.RegisterScreen
import com.example.focus.ui.screen.HomeScreen
import com.example.focus.ui.screen.ClockScreen
import com.example.focus.ui.screen.DebugScreen
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val navController = rememberNavController()

    val mainViewModel: MainViewModel = hiltViewModel()

    val token by mainViewModel.token.collectAsState(initial = "LOADING")

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

    if (token == "LOADING") {
        return
    }

    val startRoute = if (token.isNullOrEmpty()) AppRoute.Menu else AppRoute.Home

    Scaffold { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startRoute,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable<AppRoute.Menu> {
                MenuScreen(
                    onNavigateToLogin = { mainViewModel.navigateTo(AppRoute.Login) },
                    onNavigateToRegister = { mainViewModel.navigateTo(AppRoute.Register) }
                )
            }

            composable<AppRoute.Login> {
                LoginScreen(
                    onNavigateHome = {
                        mainViewModel.navigateTo(
                            destination = AppRoute.Home,
                            popUpTo = AppRoute.Menu,
                            inclusive = true
                        )
                    },
                    onNavigateToRegister = { mainViewModel.navigateTo(AppRoute.Register) }
                )
            }

            composable<AppRoute.Register> {
                RegisterScreen(
                    onNavigateToLogin = {
                        mainViewModel.navigateTo(
                            destination = AppRoute.Login,
                            popUpTo = AppRoute.Register,
                            inclusive = true
                        )
                    }
                )
            }

            composable<AppRoute.Home> {
                HomeScreen(
                    onNavigateToMenu = {
                        mainViewModel.logout()
                        mainViewModel.navigateTo(
                            destination = AppRoute.Home,
                            popUpTo = 0,
                            inclusive = true
                        )
                    },
                    onNavigateToClock = { mainViewModel.navigateTo(AppRoute.Clock) }
                )
            }

            composable<AppRoute.Debug> {
                DebugScreen(onTimerButtonClick = { mainViewModel.navigateTo(AppRoute.Clock) })
            }

            composable<AppRoute.Clock> {
                ClockScreen(navController = navController)
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