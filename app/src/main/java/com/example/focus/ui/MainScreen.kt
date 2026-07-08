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
import androidx.navigation.toRoute
import com.example.focus.navigation.AppRoute
import com.example.focus.navigation.NavigationEvent
import com.example.focus.viewmodel.MainViewModel
import com.example.focus.ui.screen.LoginScreen
import com.example.focus.ui.screen.RegisterScreen
import com.example.focus.ui.screen.HomeScreen
import com.example.focus.ui.screen.ClockScreen
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.focus.network.AuthEvent
import com.example.focus.ui.screen.AuthEntryScreen
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Backpack
import androidx.compose.material.icons.filled.Castle
import androidx.compose.material.icons.filled.HistoryEdu
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.example.focus.ui.theme.AncientGold
import com.example.focus.ui.theme.DungeonNoir700
import com.example.focus.ui.theme.InkBlack
import com.example.focus.ui.screen.RoomsScreen
import com.example.focus.ui.screen.InventoryScreen
import com.example.focus.ui.screen.DMCreateRoomScreen
import com.example.focus.ui.screen.DMPanelScreen
import com.example.focus.ui.screen.CharacterDetailScreen
import com.example.focus.ui.theme.animatedGoldBorder
import com.example.focus.ui.theme.guildGlow
import com.example.focus.ui.theme.ShieldShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Alignment
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Backpack
import androidx.compose.material.icons.filled.Castle
import androidx.compose.material.icons.filled.HistoryEdu
import androidx.compose.material.icons.filled.MilitaryTech

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {

    val navController = rememberNavController()
    val mainViewModel: MainViewModel = hiltViewModel()
    val token by mainViewModel.token.collectAsState(initial = "LOADING")
    val role by mainViewModel.role.collectAsState(initial = "student")

    HandleAppEffects(mainViewModel, navController)

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val isClockScreen = currentDestination?.hierarchy?.any { it.hasRoute(AppRoute.Clock::class) } == true

    if (token == "LOADING") {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val startRoute = if (token.isNullOrEmpty()) AppRoute.Menu else AppRoute.Home

    Scaffold(
        bottomBar = {
            if (!token.isNullOrEmpty()) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                NavigationBar(
                    containerColor = if (isClockScreen) Color.Transparent else DungeonNoir700,
                    contentColor = AncientGold,
                    tonalElevation = if (isClockScreen) 0.dp else 8.dp
                ) {
                    val items = if (role == "dm") {
                        listOf(
                            Triple(AppRoute.Home, Icons.Filled.HistoryEdu, "Inicio"),
                            Triple(AppRoute.DMPanel, Icons.Filled.Shield, "Gremios"),
                            Triple(AppRoute.DMProfile, Icons.Filled.Person, "Perfil")
                        )
                    } else {
                        listOf(
                            Triple(AppRoute.Home, Icons.Filled.HistoryEdu, "Tablón"),
                            Triple(AppRoute.Inventory, Icons.Filled.Backpack, "Inventario"),
                            Triple(AppRoute.Clock, Icons.Filled.MilitaryTech, "Misión"),
                            Triple(AppRoute.Rooms, Icons.Filled.Castle, "Gremio")
                        )
                    }

                    items.forEach { (route, icon, label) ->
                        val selected = currentDestination?.hierarchy?.any { it.hasRoute(route::class) } == true
                        
                        // Si estamos en la pantalla del reloj, ocultamos las etiquetas para que sea más limpio
                        val showLabel = !isClockScreen

                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { 
                                Box(
                                    modifier = if (selected) {
                                        Modifier
                                            .size(56.dp)
                                            .animatedGoldBorder(width = 1.5.dp, shape = CircleShape)
                                            .guildGlow(color = AncientGold, radius = 12.dp, shape = CircleShape, alpha = 0.5f)
                                    } else {
                                        Modifier.size(56.dp)
                                    },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = icon, 
                                        contentDescription = label,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                            },
                            label = if (showLabel) {
                                { Text(label, style = MaterialTheme.typography.labelSmall) }
                            } else null,
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = AncientGold,
                                selectedTextColor = AncientGold,
                                unselectedIconColor = AncientGold.copy(alpha = if (isClockScreen) 0.3f else 0.6f),
                                unselectedTextColor = AncientGold.copy(alpha = 0.6f),
                                indicatorColor = Color.Transparent
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startRoute,
            modifier = Modifier.padding(innerPadding)
        ) {
            // ... (dentro de NavHost)

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
                    onNavigateDM = {
                        mainViewModel.navigateTo(
                            destination = AppRoute.DMPanel,
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
                    onNavigateToRooms = {
                        mainViewModel.navigateTo(
                            destination = AppRoute.Rooms
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
            composable<AppRoute.Rooms> {
                RoomsScreen(
                    onNavigateBack = {
                        mainViewModel.navigateBack()
                    }
                )
            }
            composable<AppRoute.Inventory> {
                InventoryScreen(
                    onNavigateBack = {
                        mainViewModel.navigateBack()
                    },
                    onNavigateToDetail = { charName, hatName ->
                        mainViewModel.navigateTo(AppRoute.CharacterDetail(charName, hatName))
                    }
                )
            }
            
            composable<AppRoute.CharacterDetail> { backStackEntry ->
                val route: AppRoute.CharacterDetail = backStackEntry.toRoute()
                CharacterDetailScreen(
                    characterName = route.characterName,
                    hatName = route.hatName,
                    onNavigateBack = { mainViewModel.navigateBack() }
                )
            }

            // --- Rutas del DM ---
            composable<AppRoute.DMPanel> {
                DMPanelScreen(
                    onNavigateToCreateRoom = {
                        mainViewModel.navigateTo(AppRoute.DMCreateRoom)
                    }
                )
            }
            composable<AppRoute.DMProfile> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Perfil del DM (En construcción)", color = Color.White)
                }
            }
            composable<AppRoute.DMCreateRoom> {
                DMCreateRoomScreen(
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
