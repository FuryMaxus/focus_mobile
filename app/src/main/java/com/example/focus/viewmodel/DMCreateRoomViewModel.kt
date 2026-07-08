package com.example.focus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.focus.data.remote.RoomCreatePayload
import com.example.focus.repository.RoomRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DMCreateRoomViewModel @Inject constructor(
    private val roomRepository: RoomRepository
) : ViewModel() {

    private val _name = MutableStateFlow("")
    val name = _name.asStateFlow()

    private val _description = MutableStateFlow("")
    val description = _description.asStateFlow()

    private val _capacity = MutableStateFlow("5")
    val capacity = _capacity.asStateFlow()

    private val _xpMultiplier = MutableStateFlow("1.3")
    val xpMultiplier = _xpMultiplier.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _mensaje = MutableStateFlow("")
    val mensaje = _mensaje.asStateFlow()

    private val _isError = MutableStateFlow(false)
    val isError = _isError.asStateFlow()

    private val _isTimeRestricted = MutableStateFlow(false)
    val isTimeRestricted = _isTimeRestricted.asStateFlow()

    private val _startTime = MutableStateFlow("08:00")
    val startTime = _startTime.asStateFlow()

    private val _endTime = MutableStateFlow("18:00")
    val endTime = _endTime.asStateFlow()

    fun onNameChange(value: String) { _name.value = value }
    fun onDescriptionChange(value: String) { _description.value = value }
    fun onCapacityChange(value: String) { _capacity.value = value }
    fun onMultiplierChange(value: String) { _xpMultiplier.value = value }

    fun onTimeRestrictedChange(restricted: Boolean) { _isTimeRestricted.value = restricted }

    fun onStartTimeChange(time: String) { _startTime.value = time }

    fun onEndTimeChange(time: String) { _endTime.value = time }

    fun createRoom(onSuccess: () -> Unit) {
        if (_name.value.isBlank()) {
            _isError.value = true
            _mensaje.value = "El nombre del gremio es obligatorio"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _isError.value = false
            _mensaje.value = ""

            val payload = RoomCreatePayload(
                name = _name.value,
                description = _description.value.ifBlank { null },
                capacity = _capacity.value.toIntOrNull() ?: 5,
                xpMultiplier = _xpMultiplier.value.toFloatOrNull() ?: 1.3f,
                validFromTime = _startTime.value.ifBlank { null },
                validUntilTime = _endTime.value.ifBlank { null }
            )

            roomRepository.createRoom(payload).fold(
                onSuccess = {
                    _mensaje.value = "¡Gremio forjado con éxito!"
                    onSuccess()
                },
                onFailure = {
                    _isError.value = true
                    _mensaje.value = "Error al crear gremio: ${it.message}"
                }
            )
            _isLoading.value = false
        }
    }
}
