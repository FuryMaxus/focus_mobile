package com.example.focus.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.focus.viewmodel.RoomViewModel
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.example.focus.data.remote.RoomResponseDto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomsScreen(
    onNavigateBack: () -> Unit,
    viewModel: RoomViewModel = hiltViewModel()
) {

    val rooms by viewModel.rooms.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val role by viewModel.userRole.collectAsState()
    val equippedRoomId by viewModel.equippedRoomId.collectAsState()

    var showCreateDialog by remember { mutableStateOf(false) }
    var showJoinDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Gremios") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Atrás")
                    }
                },
                actions = {
                    TextButton(onClick = { showJoinDialog = true }) {
                        Text("Unirse con Código")
                    }
                }
            )
        },
        floatingActionButton = {
            if (role == "dm" || role == "admin") {
                ExtendedFloatingActionButton(
                    onClick = { showCreateDialog = true },
                    icon = { Icon(Icons.Filled.Add, "Crear") },
                    text = { Text("Fundar") }
                )
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            if (isLoading && rooms.isEmpty()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (rooms.isEmpty()) {
                Text(
                    text = "Aún no perteneces a ningún gremio.",
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(rooms) { room ->
                        val isEquipped = room.id == equippedRoomId
                        RoomCard(
                            room = room,
                            isEquipped = isEquipped,
                            onEquipClick = {
                                if (isEquipped) viewModel.unequipRoom()
                                else viewModel.equipRoom(room)
                            }
                        )
                    }
                }
            }
        }

        if (showCreateDialog) {
            CreateRoomDialog(
                onDismiss = { showCreateDialog = false },
                onCreate = { name, isRestricted, start, end ->
                    viewModel.createRoom(name, isRestricted, start, end) { showCreateDialog = false }
                }
            )
        }

        if (showJoinDialog) {
            JoinRoomDialog(
                onDismiss = { showJoinDialog = false },
                onJoin = { code -> viewModel.joinRoom(code) { showJoinDialog = false } }
            )
        }
    }
}

@Composable
fun RoomCard(
    room: RoomResponseDto,
    isEquipped: Boolean,
    onEquipClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        border = if (isEquipped) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
        colors = if (isEquipped) CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        else CardDefaults.cardColors()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(room.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)

                Button(
                    onClick = onEquipClick,
                    colors = if (isEquipped) ButtonDefaults.buttonColors(containerColor = Color.Gray)
                    else ButtonDefaults.buttonColors()
                ) {
                    Text(if (isEquipped) "Desequipar" else "Equipar")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text("Bonus activo: ${room.xpMultiplier}x XP", fontWeight = FontWeight.SemiBold)

            if (room.validFromTime != null && room.validUntilTime != null) {
                Text(
                    text = "Horario válido: ${room.validFromTime} a ${room.validUntilTime}",
                    color = MaterialTheme.colorScheme.error
                )
            } else {
                Text("Horario: 24/7 (Sin restricciones)")
            }

            if (room.invitationCode != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text("Código de Invitación: ${room.invitationCode}", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

@Composable
fun CreateRoomDialog(
    onDismiss: () -> Unit,
    onCreate: (String, Boolean, String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var isRestricted by remember { mutableStateOf(false) }
    var startTime by remember { mutableStateOf("08:00") }
    var endTime by remember { mutableStateOf("18:00") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Fundar Gremio") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Switch(checked = isRestricted, onCheckedChange = { isRestricted = it })
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Horario Anti-Farm")
                }
                if (isRestricted) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = startTime, onValueChange = { startTime = it },
                            label = { Text("Desde (HH:MM)") }, modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = endTime, onValueChange = { endTime = it },
                            label = { Text("Hasta (HH:MM)") }, modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { onCreate(name, isRestricted, startTime, endTime) }) {
                Text("Crear")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}

@Composable
fun JoinRoomDialog(
    onDismiss: () -> Unit,
    onJoin: (String) -> Unit
) {
    var code by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Unirse al Gremio") },
        text = {
            OutlinedTextField(
                value = code,
                onValueChange = { code = it },
                label = { Text("Código de Invitación") },
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(onClick = { onJoin(code) }) { Text("Unirse") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}