package com.example.focus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.focus.data.local.UserPreferences
import com.example.focus.data.remote.RoomCreatePayload
import com.example.focus.data.remote.RoomResponseDto
import com.example.focus.repository.RoomRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class RoomViewModel @Inject constructor(
    private val roomRepository: RoomRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _rooms = MutableStateFlow<List<RoomResponseDto>>(emptyList())
    val rooms: StateFlow<List<RoomResponseDto>> = _rooms.asStateFlow()
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    private val _mensaje = MutableStateFlow("")
    val mensaje: StateFlow<String> = _mensaje.asStateFlow()

    val userRole: StateFlow<String?> = userPreferences.getUserRole
        .stateIn(viewModelScope, SharingStarted.Lazily, "student")

    val equippedRoomId: StateFlow<String?> = userPreferences.getEquippedRoomId
        .stateIn(viewModelScope, SharingStarted.Lazily, null)

    init {
        loadRooms()
    }

    fun loadRooms() {
        viewModelScope.launch {
            _isLoading.value = true
            roomRepository.fetchRooms().fold(
                onSuccess = { _rooms.value = it },
                onFailure = { _mensaje.value = "Error al cargar los gremios" }
            )
            _isLoading.value = false
        }
    }

    fun equipRoom(roomId: String) {
        viewModelScope.launch {
            userPreferences.equipRoom(roomId)
        }
    }

    fun unequipRoom() {
        viewModelScope.launch {
            userPreferences.unequipRoom()
        }
    }

    fun createRoom(name: String, isRestricted: Boolean, startTime: String, endTime: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            val payload = RoomCreatePayload(
                name = name,
                validFromTime = if (isRestricted) startTime else null,
                validUntilTime = if (isRestricted) endTime else null
            )
            roomRepository.createRoom(payload).fold(
                onSuccess = {
                    loadRooms()
                    onSuccess()
                },
                onFailure = { _mensaje.value = "Error al fundar la sala" }
            )
            _isLoading.value = false
        }
    }

    fun joinRoom(invitationCode: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            roomRepository.joinRoom(invitationCode).fold(
                onSuccess = {
                    loadRooms()
                    onSuccess()
                },
                onFailure = { _mensaje.value = "Código inválido o sala llena" }
            )
            _isLoading.value = false
        }
    }
}