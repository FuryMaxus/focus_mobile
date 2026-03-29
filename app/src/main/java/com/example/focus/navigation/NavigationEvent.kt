package com.example.focus.navigation

sealed class NavigationEvent{
    data class NavigateTo(
        val appRoute: AppRoute,
        val popRoute: AppRoute ?= null,
        val inclusive: Boolean = false,
        val singleTop: Boolean = false,
        val args: Map<String, String>? = null
    ): NavigationEvent()
    object PopBackStack: NavigationEvent()
    object NavigateUp: NavigationEvent()
}