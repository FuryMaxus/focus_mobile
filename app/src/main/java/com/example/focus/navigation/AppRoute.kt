package com.example.focus.navigation

import kotlinx.serialization.Serializable

sealed interface AppRoute {
    @Serializable
    data object Home : AppRoute

    @Serializable
    data object Debug : AppRoute

    @Serializable
    data object Clock : AppRoute
    @Serializable data object Menu : AppRoute

    @Serializable data object Login : AppRoute

    @Serializable data object Register : AppRoute

    @Serializable data object Rooms : AppRoute

    @Serializable data object Inventory : AppRoute
}