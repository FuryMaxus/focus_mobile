package com.example.focus.navigation

sealed class AppRoute(val route: String) {
    data object Home: AppRoute("home")
}