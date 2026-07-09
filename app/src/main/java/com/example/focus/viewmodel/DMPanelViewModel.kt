package com.example.focus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.focus.data.remote.MemberResponseDto
import com.example.focus.data.remote.RoomResponseDto
import com.example.focus.repository.RoomRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DMPanelViewModel @Inject constructor(
    private val roomRepository: RoomRepository
) : ViewModel() {

    private val _rooms = MutableStateFlow<List<RoomResponseDto>>(emptyList())
    val rooms = _rooms.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _selectedRoom = MutableStateFlow<RoomResponseDto?>(null)
    val selectedRoom = _selectedRoom.asStateFlow()

    private val _roomMembers = MutableStateFlow<List<MemberResponseDto>>(emptyList())
    val roomMembers = _roomMembers.asStateFlow()

    private val _errorMessage = MutableStateFlow("")
    val errorMessage = _errorMessage.asStateFlow()

    private val _roomStats = MutableStateFlow<List<com.example.focus.data.remote.SessionReportItemDto>>(emptyList())
    val roomStats = _roomStats.asStateFlow()

    init {
        refreshRooms()
    }

    fun refreshRooms() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = ""
            roomRepository.fetchRoomsCreated().fold(
                onSuccess = { salas ->
                    _rooms.value = salas
                },
                onFailure = { error ->
                    _errorMessage.value = "Error: ${error.message}"
                    println("❌ ERROR AL TRAER SALAS DEL DM: ${error.message}")
                }
            )
            _isLoading.value = false
        }
    }

    fun selectRoom(room: RoomResponseDto?) {
        _selectedRoom.value = room
        if (room != null) {
            fetchMembers(room.id)
            fetchRoomStats(room.id)
        } else {
            _roomMembers.value = emptyList()
            _roomStats.value = emptyList()
        }
    }

    private fun fetchMembers(roomId: String) {
        viewModelScope.launch {
            roomRepository.fetchRoomMembers(roomId).onSuccess {
                _roomMembers.value = it
            }
        }
    }

    private fun fetchRoomStats(roomId: String) {
        viewModelScope.launch {
            roomRepository.fetchRoomStats(roomId).onSuccess { response ->
                _roomStats.value = response.reports
            }
        }
    }

    fun closeCurrentRoom() {
        val current = _selectedRoom.value ?: return
        viewModelScope.launch {
            _isLoading.value = true
            roomRepository.closeRoom(current.id).onSuccess {
                selectRoom(null)
                refreshRooms()
            }
            _isLoading.value = false
        }
    }
}