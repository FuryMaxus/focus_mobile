package com.example.focus.navigation

import kotlinx.serialization.Serializable

sealed interface AppRoute {
    @Serializable
    data object Home : AppRoute

    @Serializable
    data object Debug : AppRoute

    @Serializable
    data object Clock : AppRoute
}