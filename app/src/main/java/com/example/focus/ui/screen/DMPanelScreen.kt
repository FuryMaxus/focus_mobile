package com.example.focus.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.focus.data.remote.RoomResponseDto
import com.example.focus.ui.component.*
import com.example.focus.ui.theme.*
import com.example.focus.viewmodel.DMPanelViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DMPanelScreen(
    viewModel: DMPanelViewModel = hiltViewModel(),
    onNavigateToCreateRoom: () -> Unit
) {
    val rooms by viewModel.rooms.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val selectedRoom by viewModel.selectedRoom.collectAsState()
    val members by viewModel.roomMembers.collectAsState()

    DungeonBackground {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text("PANEL DEL MAESTRO", color = AncientGold, style = MaterialTheme.typography.titleMedium)
                            Text("Gestión de Gremios y Sesiones", color = SteelSilver500, style = MaterialTheme.typography.labelSmall)
                        }
                    },
                    actions = {
                        IconButton(onClick = { viewModel.refreshRooms() }) {
                            Icon(Icons.Filled.Refresh, contentDescription = "Refrescar", tint = AncientGold)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = DungeonNoir700.copy(alpha = 0.9f))
                )
            },
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    onClick = onNavigateToCreateRoom,
                    containerColor = AmberFlame,
                    contentColor = InkBlack,
                    icon = { Icon(Icons.Filled.Add, "Crear") },
                    text = { Text("FORJAR GREMIO", fontWeight = FontWeight.Bold) },
                    modifier = Modifier.guildGlow(color = AmberFlame, radius = 12.dp)
                )
            }
        ) { padding ->
            Box(modifier = Modifier.padding(padding)) {
                if (isLoading && rooms.isEmpty()) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = AncientGold)
                } else if (rooms.isEmpty()) {
                    EmptyRoomsState(onNavigateToCreateRoom)
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(rooms) { room ->
                            RoomItem(
                                room = room,
                                onClick = { viewModel.selectRoom(room) }
                            )
                        }
                    }
                }

                // Modal / Detalle de la sala seleccionada
                selectedRoom?.let { room ->
                    RoomDetailDialog(
                        room = room,
                        membersCount = members.size,
                        onClose = { viewModel.selectRoom(null) },
                        onEndRoom = { viewModel.closeCurrentRoom() }
                    )
                }
            }
        }
    }
}

@Composable
private fun RoomItem(room: RoomResponseDto, onClick: () -> Unit) {
    GuildCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        animatedBorder = room.status == "active",
        glowColor = if (room.status == "active") AncientGold else Color.Gray
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = room.name,
                    style = MaterialTheme.typography.titleLarge,
                    color = AncientGold,
                    fontWeight = FontWeight.Bold
                )
                RarityBadge(
                    text = room.status.uppercase(),
                    accent = if (room.status == "active") DungeonGreen else DragonRed,
                    fill = DungeonNoir500
                )
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = room.description ?: "Sin descripción",
                style = MaterialTheme.typography.bodyMedium,
                color = SteelSilver,
                maxLines = 2
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                StatSmall(Icons.Filled.Group, "${room.capacity} Máx")
                StatSmall(Icons.Filled.Numbers, "XP x${room.xpMultiplier}")
            }
        }
    }
}

@Composable
private fun StatSmall(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(14.dp), tint = SteelSilver500)
        Text(text, style = MaterialTheme.typography.labelSmall, color = SteelSilver500)
    }
}

@Composable
private fun EmptyRoomsState(onAction: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("No has forjado ningún gremio aún", color = SteelSilver500, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(16.dp))
        GuildOutlineButton(text = "Comenzar forja", onClick = onAction)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RoomDetailDialog(
    room: RoomResponseDto,
    membersCount: Int,
    onClose: () -> Unit,
    onEndRoom: () -> Unit
) {
    BasicAlertDialog(
        onDismissRequest = onClose,
        modifier = Modifier.fillMaxWidth(0.95f),
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
    ) {
        GuildCard(
            modifier = Modifier.fillMaxWidth(),
            animatedBorder = true,
            glowColor = AncientGold
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    SectionLabel(text = "DETALLES DEL GREMIO", color = AncientGold)
                    IconButton(onClick = onClose, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Filled.Close, contentDescription = "Cerrar", tint = SteelSilver500)
                    }
                }

                Text(room.name, style = MaterialTheme.typography.headlineSmall, color = AncientGold, fontWeight = FontWeight.Bold)
                
                // Código de invitación resaltado
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(InkBlack, RoundedCornerShape(8.dp))
                        .border(1.dp, AncientGold700, RoundedCornerShape(8.dp))
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("CÓDIGO DE INVITACIÓN", style = MaterialTheme.typography.labelSmall, color = SteelSilver500)
                        Text(
                            text = room.invitationCode ?: "---",
                            style = MaterialTheme.typography.displaySmall,
                            color = AncientGold,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 4.sp
                        )
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("CONECTADOS", style = MaterialTheme.typography.labelSmall, color = SteelSilver500)
                        Text("$membersCount / ${room.capacity}", style = MaterialTheme.typography.titleMedium, color = AncientGold)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("BONUS XP", style = MaterialTheme.typography.labelSmall, color = SteelSilver500)
                        Text("x${room.xpMultiplier}", style = MaterialTheme.typography.titleMedium, color = AncientGold)
                    }
                }

                if (room.status == "active") {
                    GuildPrimaryButton(
                        text = "FINALIZAR SESIÓN",
                        onClick = onEndRoom,
                        modifier = Modifier.fillMaxWidth(),
                        leading = "🛑"
                    )
                }

                Text(
                    text = "Comparte el código con tus alumnos para que puedan unirse a este gremio.",
                    style = MaterialTheme.typography.bodySmall.copy(fontStyle = FontStyle.Italic),
                    color = SteelSilver500,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
