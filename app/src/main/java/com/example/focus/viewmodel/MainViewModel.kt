package com.example.focus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.focus.navigation.NavigationEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {

    private val _navEvents = Channel<NavigationEvent>()
    val navEvents = _navEvents.receiveAsFlow()

    fun navigateTo(
        destination: Any,
        popUpTo: Any? = null,
        inclusive: Boolean = false,
        singleTop: Boolean = false
    ) {
        viewModelScope.launch {
            _navEvents.send(
                NavigationEvent.NavigateTo(
                    destination = destination,
                    popUpTo = popUpTo,
                    inclusive = inclusive,
                    singleTop = singleTop
                )
            )
        }
    }

    fun navigateBack() {
        viewModelScope.launch {
            _navEvents.send(NavigationEvent.PopBackStack)
        }
    }

    fun navigateUp() {
        viewModelScope.launch {
            _navEvents.send(NavigationEvent.NavigateUp)
        }
    }
}