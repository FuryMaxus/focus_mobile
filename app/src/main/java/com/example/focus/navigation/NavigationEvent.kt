package com.example.focus.navigation

sealed class NavigationEvent {
    data class NavigateTo(
        val destination: Any,
        val popUpTo: Any? = null,
        val inclusive: Boolean = false,
        val singleTop: Boolean = false
    ) : NavigationEvent()

    data object PopBackStack : NavigationEvent()
    data object NavigateUp : NavigationEvent()
}