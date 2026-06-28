package com.example.focus.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
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
import com.example.focus.ui.screen.InventoryScreen
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.focus.network.AuthEvent
import com.example.focus.ui.screen.AuthEntryScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {

    val navController = rememberNavController()
    val mainViewModel: MainViewModel = hiltViewModel()
    val token by mainViewModel.token.collectAsState(initial = "LOADING")

    HandleAppEffects(mainViewModel, navController)

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
                AuthEntryScreen(
                    onNavigateToLogin = {
                        mainViewModel.navigateTo(
                            destination = AppRoute.Login
                        )
                    },
                    onNavigateToRegister = {
                        mainViewModel.navigateTo(
                            destination = AppRoute.Register
                        )
                    }
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
                    onNavigateToRegister = {
                        mainViewModel.navigateTo(
                            destination = AppRoute.Register
                        )
                    }
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
                    onNavigateToAuthEntry = {
                        mainViewModel.logout()
                    },
                    onNavigateToClock = {
                        mainViewModel.navigateTo(
                            destination = AppRoute.Clock
                        )
                    },
                    onNavigateToInventory = {
                        mainViewModel.navigateTo(
                            destination = AppRoute.Inventory
                        )
                    }
                )
            }
            composable<AppRoute.Clock> {
                ClockScreen(
                    onNavigateBack = {
                       mainViewModel.navigateBack()
                    }
                )
            }
            composable<AppRoute.Inventory> {
                InventoryScreen(
                    onNavigateBack = {
                        mainViewModel.navigateBack()
                    }
                )
            }
        }
    }
}

@Composable
private fun HandleAppEffects(
    mainViewModel: MainViewModel,
    navController: NavHostController
) {
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
    LaunchedEffect(Unit) {
        mainViewModel.authEvent.collect { event ->
            when (event) {
                is AuthEvent.LoggedOut -> {
                    mainViewModel.navigateTo(
                        destination = AppRoute.Menu,
                        popUpTo = AppRoute.Home,
                        inclusive = true
                    )
                }
            }
        }
    }
}

