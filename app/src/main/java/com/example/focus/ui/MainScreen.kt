package com.example.focus.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.focus.navigation.AppRoute
import com.example.focus.navigation.NavigationEvent
import com.example.focus.ui.screen.ClockScreen
import com.example.focus.ui.screen.DebugScreen
import com.example.focus.viewmodel.MainViewModel
import com.example.focus.viewmodel.TokenManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val context = LocalContext.current
    val navController = rememberNavController()
    
    // Crear TokenManager y ViewModel con Factory
    val tokenManager = remember { TokenManager(context) }
    val mainViewModel: MainViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(tokenManager) as T
            }
        }
    )

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
    
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("FOCUS APP", style = MaterialTheme.typography.titleLarge) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = AppRoute.Debug,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            composable<AppRoute.Home> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "¡Bienvenido de nuevo!",
                                style = MaterialTheme.typography.headlineSmall
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Tu sesión está activa y lista para trabajar.",
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            Button(
                                onClick = { mainViewModel.logout() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.error
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Cerrar Sesión")
                            }
                        }
                    }
                }
            }
            composable<AppRoute.Debug> {
                DebugScreen(
                    onTimerButtonClick = { mainViewModel.navigateTo(AppRoute.Clock) },
                    onLoginClick = { email, psw -> mainViewModel.login(email, psw) },
                    onRegisterClick = { email, psw -> mainViewModel.register(email, psw) },
                    onFetchUsersClick = { mainViewModel.fetchUsers() },
                    usersList = mainViewModel.usersList.value
                )
            }
            composable<AppRoute.Clock> {
                ClockScreen(onBack = { mainViewModel.navigateBack() })
            }
        }
    }
}