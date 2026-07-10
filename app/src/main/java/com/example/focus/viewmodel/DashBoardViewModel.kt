package com.example.focus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.focus.data.remote.GraphItemDto
import com.example.focus.data.remote.LeaderboardItemDto
import com.example.focus.data.remote.RoomResponseDto
import com.example.focus.repository.RoomRepository // O StatsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: RoomRepository
) : ViewModel() {

    private val _dmRooms = MutableStateFlow<List<RoomResponseDto>>(emptyList())
    val dmRooms: StateFlow<List<RoomResponseDto>> = _dmRooms.asStateFlow()

    private val _selectedRoom = MutableStateFlow<RoomResponseDto?>(null)
    val selectedRoom: StateFlow<RoomResponseDto?> = _selectedRoom.asStateFlow()

    private val _leaderboard = MutableStateFlow<List<LeaderboardItemDto>>(emptyList())
    val leaderboard: StateFlow<List<LeaderboardItemDto>> = _leaderboard.asStateFlow()

    private val _graphData = MutableStateFlow<List<GraphItemDto>>(emptyList())
    val graphData: StateFlow<List<GraphItemDto>> = _graphData.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadDmRooms()
    }

     fun loadDmRooms() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.fetchRoomsCreated().fold(
                onSuccess = { rooms ->
                    _dmRooms.value = rooms
                    if (rooms.isNotEmpty()) {
                        selectRoom(rooms.first())
                    }
                },
                onFailure = {  }
            )
            _isLoading.value = false
        }
    }

    fun selectRoom(room: RoomResponseDto) {
        _selectedRoom.value = room
        loadRoomStats(room.id)
    }

    private fun loadRoomStats(roomId: String) {
        viewModelScope.launch {
            _isLoading.value = true

            repository.fetchRoomLeaderboard(roomId).onSuccess { data ->
                _leaderboard.value = data
            }

            val endDate = ZonedDateTime.now()
            val startDate = endDate.minusDays(7)
            val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME

            repository.fetchRoomGraph(
                roomId = roomId,
                startDate = formatter.format(startDate),
                endDate = formatter.format(endDate)
            ).onSuccess { data ->
                _graphData.value = data
            }

            _isLoading.value = false
        }
    }
}