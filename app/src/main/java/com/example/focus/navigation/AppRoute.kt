package com.example.focus.navigation

import kotlinx.serialization.Serializable

sealed class AppRoute(val route: String) {
    @Serializable
    data object Home

    @Serializable
    data object Debug

    @Serializable
    data object Clock
}