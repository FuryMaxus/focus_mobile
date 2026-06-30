package com.example.focus.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.focus.navigation.AppRoute
import com.example.focus.navigation.NavigationEvent
import com.example.focus.network.AuthEvent
import com.example.focus.ui.screen.*
import com.example.focus.ui.theme.*
import com.example.focus.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val mainViewModel: MainViewModel = hiltViewModel()
    val token by mainViewModel.token.collectAsState(initial = "LOADING")

    HandleAppEffects(mainViewModel, navController)

    if (token == "LOADING") return

    val startRoute = if (token.isNullOrEmpty()) AppRoute.Menu else AppRoute.Home
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val showBottomBar = currentDestination?.let { dest ->
        dest.hasRoute<AppRoute.Home>() ||
        dest.hasRoute<AppRoute.Inventory>() ||
        dest.hasRoute<AppRoute.Profile>() ||
        dest.hasRoute<AppRoute.Stats>()
    } ?: false

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                FocusBottomBar(
                    currentDestination = currentDestination,
                    onNavigate = { route ->
                        mainViewModel.navigateTo(
                            destination = route,
                            singleTop = true
                        )
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startRoute,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable<AppRoute.Menu> {
                AuthEntryScreen(
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
                    onNavigateToAuthEntry = { mainViewModel.logout() },
                    onNavigateToClock = { mainViewModel.navigateTo(AppRoute.Clock) },
                    onNavigateToInventory = { mainViewModel.navigateTo(AppRoute.Inventory) }
                )
            }
            composable<AppRoute.Clock> {
                ClockScreen(onNavigateBack = { mainViewModel.navigateBack() })
            }
            composable<AppRoute.Inventory> {
                InventoryScreen(onNavigateBack = { mainViewModel.navigateBack() })
            }
            composable<AppRoute.Profile> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Pantalla de Perfil (Próximamente)", color = AncientGold)
                }
            }
            composable<AppRoute.Stats> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Pantalla de Estadísticas (Próximamente)", color = AncientGold)
                }
            }
        }
    }
}

@Composable
fun FocusBottomBar(
    currentDestination: androidx.navigation.NavDestination?,
    onNavigate: (AppRoute) -> Unit
) {
    NavigationBar(
        containerColor = DungeonNoir700,
        contentColor = AncientGold,
        tonalElevation = 8.dp
    ) {
        val items = listOf(
            BottomNavItem("Inicio", AppRoute.Home, Icons.Filled.Home),
            BottomNavItem("Inventario", AppRoute.Inventory, Icons.Filled.Inventory),
            BottomNavItem("Perfil", AppRoute.Profile, Icons.Filled.Person),
            BottomNavItem("Stats", AppRoute.Stats, Icons.Filled.BarChart)
        )

        items.forEach { item ->
            val selected = currentDestination?.hierarchy?.any { it.hasRoute(item.route::class) } == true
            NavigationBarItem(
                selected = selected,
                onClick = { onNavigate(item.route) },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        tint = if (selected) AmberFlame else SteelSilver500
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        color = if (selected) AncientGold else SteelSilver500,
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                colors = NavigationBarItemDefaults.colors(indicatorColor = DungeonNoir500)
            )
        }
    }
}

data class BottomNavItem(
    val label: String,
    val route: AppRoute,
    val icon: ImageVector
)

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
