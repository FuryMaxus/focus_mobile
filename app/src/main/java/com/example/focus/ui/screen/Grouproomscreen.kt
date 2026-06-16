package com.example.focus.ui.screen

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.focus.ui.theme.*


enum class MemberStatus { STUDYING, PAUSED, IDLE, OFFLINE }

data class GuildMember(
    val id: String,
    val name: String,
    val level: Int,
    val role: String,           // "Mago", "Guerrero", "Druida", etc.
    val avatarEmoji: String,
    val status: MemberStatus,
    val sessionSeconds: Int,    // segundos de la sesión actual
    val totalXp: Int
)

data class StudyRoom(
    val id: String,
    val name: String,
    val description: String,
    val memberCount: Int,
    val maxMembers: Int,
    val isActive: Boolean,
    val roomType: String        // "Mazmorra", "Biblioteca", "Torre del Mago"
)


private val previewMembers = listOf(
    GuildMember("1", "Aldric el Sabio",    14, "Mago",     "🧙", MemberStatus.STUDYING, 2340, 4800),
    GuildMember("2", "Seline",             9,  "Druida",   "🌿", MemberStatus.STUDYING, 1200, 2100),
    GuildMember("3", "Thorne",             11, "Guerrero", "⚔️", MemberStatus.PAUSED,   800,  3300),
    GuildMember("4", "Mira la Veloz",      6,  "Pícara",   "🗡️", MemberStatus.IDLE,     0,    1500),
    GuildMember("5", "Brom",               3,  "Bardo",    "🎵", MemberStatus.OFFLINE,  0,    600),
)

private val previewRooms = listOf(
    StudyRoom("r1", "La Mazmorra Profunda", "Zona de silencio absoluto. Solo guerreros serios.", 4, 6, true,  "Mazmorra"),
    StudyRoom("r2", "Torre del Archimago",  "Para estudios de magia avanzada y concentración.", 2, 4, true,  "Torre del Mago"),
    StudyRoom("r3", "Biblioteca del Gremio","Abierta a todos. Comparte tu sabiduría.",          7, 10, true, "Biblioteca"),
    StudyRoom("r4", "Sala de los Novatos",  "El primer escalón de todo gran aventurero.",       1, 8, false, "Biblioteca"),
)

// ═══════════════════════════════════════════════════════════════
//  GroupRoomScreen — pantalla principal de salas grupales
//  TODO: reemplazá previewMembers / previewRooms con tu ViewModel
// ═══════════════════════════════════════════════════════════════
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupRoomScreen(navController: NavController) {

    // TODO: val viewModel: GroupRoomViewModel = viewModel(...)
    // TODO: val rooms   by viewModel.rooms.collectAsState()
    // TODO: val members by viewModel.members.collectAsState()
    val rooms   = previewRooms
    val members = previewMembers

    var selectedTab by remember { mutableStateOf(0) }  // 0=Salas 1=Gremio
    var selectedRoom by remember { mutableStateOf<StudyRoom?>(null) }


    if (selectedRoom != null) {
        RoomDetailScreen(
            room    = selectedRoom!!,
            members = members,
            onBack  = { selectedRoom = null }
        )
        return
    }

    Scaffold(
        containerColor = DungeonNoir,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "SALAS GRUPALES",
                            style = MaterialTheme.typography.titleLarge,
                            color = AncientGold
                        )
                        Text(
                            "Gremio de Aventureros",
                            style = MaterialTheme.typography.labelSmall,
                            color = SteelSilver500,
                            letterSpacing = 1.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, "Atrás", tint = SteelSilver)
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO: viewModel.createRoom() */ }) {
                        Icon(Icons.Filled.Add, "Crear sala", tint = AncientGold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DungeonNoir700)
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(listOf(DungeonNoir700, DungeonNoir, InkBlack))
                )
                .padding(paddingValues)
        ) {

            // ── Tabs: Salas / Gremio ────────────────────────────
            GuildTabRow(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )

            when (selectedTab) {
                0 -> RoomsTab(
                    rooms = rooms,
                    onRoomSelected = { selectedRoom = it }
                )
                1 -> GuildTab(members = members)
            }
        }
    }
}

// ── Tabs con estética medieval ───────────────────────────────────
@Composable
private fun GuildTabRow(selectedTab: Int, onTabSelected: (Int) -> Unit) {
    val tabs = listOf("⚔  SALAS DE ESTUDIO", "🏰  MI GREMIO")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(DungeonNoir700)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        tabs.forEachIndexed { index, label ->
            val selected = selectedTab == index
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(4.dp))
                    .background(
                        if (selected)
                            Brush.horizontalGradient(listOf(SaddleBrown700, SaddleBrown))
                        else
                            Brush.horizontalGradient(listOf(DungeonNoir500, DungeonNoir500))
                    )
                    .border(
                        1.dp,
                        if (selected) AncientGold700 else SteelSilver200,
                        RoundedCornerShape(4.dp)
                    )
                    .clickable { onTabSelected(index) }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    color = if (selected) AncientGold else SteelSilver500,
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════
//  TAB 1: Lista de salas
// ═══════════════════════════════════════════════════════════════
@Composable
private fun RoomsTab(
    rooms: List<StudyRoom>,
    onRoomSelected: (StudyRoom) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            // Banner de stats activos
            ActiveStudyBanner(
                totalStudying = rooms.sumOf { it.memberCount }
            )
            Spacer(modifier = Modifier.height(4.dp))
        }

        items(rooms) { room ->
            RoomCard(room = room, onClick = { onRoomSelected(room) })
        }
    }
}

// ── Banner: "X aventureros estudiando ahora" ────────────────────
@Composable
private fun ActiveStudyBanner(totalStudying: Int) {
    val pulseAnim = rememberInfiniteTransition(label = "banner")
    val glowAlpha by pulseAnim.animateFloat(
        initialValue = 0.5f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1200, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "bannerGlow"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(SaddleBrown700, DungeonNoir700, SaddleBrown700)
                )
            )
            .border(
                1.dp,
                Brush.horizontalGradient(listOf(SaddleBrown, AncientGold700, SaddleBrown)),
                RoundedCornerShape(6.dp)
            )
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Dot animado
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(AmberFlame.copy(alpha = glowAlpha))
            )
            Column {
                Text(
                    text = "$totalStudying aventureros estudiando ahora",
                    style = MaterialTheme.typography.titleSmall,
                    color = AncientGold
                )
                Text(
                    text = "Únete a una sala y conquista el conocimiento juntos",
                    style = MaterialTheme.typography.bodySmall.copy(fontStyle = FontStyle.Italic),
                    color = SteelSilver500
                )
            }
        }
    }
}

// ── Card de sala individual ──────────────────────────────────────
@Composable
private fun RoomCard(room: StudyRoom, onClick: () -> Unit) {
    val roomEmoji = when (room.roomType) {
        "Mazmorra"      -> "🏚"
        "Torre del Mago"-> "🗼"
        "Biblioteca"    -> "📚"
        else            -> "🏰"
    }
    val occupancyFraction = room.memberCount.toFloat() / room.maxMembers.toFloat()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(DungeonNoir700)
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    listOf(
                        if (room.isActive) SaddleBrown else SteelSilver200,
                        if (room.isActive) AncientGold700 else SteelSilver200,
                        if (room.isActive) SaddleBrown else SteelSilver200
                    )
                ),
                shape = RoundedCornerShape(6.dp)
            )
            .clickable(enabled = room.isActive) { onClick() }
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {

            // Fila superior: icono + nombre + badge de estado
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = roomEmoji, fontSize = 28.sp)
                    Column {
                        Text(
                            text = room.name,
                            style = MaterialTheme.typography.titleMedium,
                            color = if (room.isActive) AncientGold else SteelSilver500,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = room.roomType,
                            style = MaterialTheme.typography.labelSmall,
                            color = SaddleBrown300,
                            letterSpacing = 0.8.sp
                        )
                    }
                }

                // Badge activo/inactivo
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(2.dp))
                        .background(
                            if (room.isActive) SaddleBrown700 else DungeonNoir500
                        )
                        .border(
                            1.dp,
                            if (room.isActive) AmberFlame else SteelSilver200,
                            RoundedCornerShape(2.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = if (room.isActive) "ABIERTA" else "CERRADA",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (room.isActive) AmberFlame else SteelSilver500,
                        letterSpacing = 0.8.sp
                    )
                }
            }

            // Descripción
            Text(
                text = room.description,
                style = MaterialTheme.typography.bodySmall.copy(fontStyle = FontStyle.Italic),
                color = SteelSilver500,
                maxLines = 2
            )

            // Barra de ocupación
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Aventureros: ${room.memberCount}/${room.maxMembers}",
                        style = MaterialTheme.typography.labelSmall,
                        color = SteelSilver500
                    )
                    Text(
                        text = "${(occupancyFraction * 100).toInt()}% ocupada",
                        style = MaterialTheme.typography.labelSmall,
                        color = when {
                            occupancyFraction >= 0.9f -> DragonRed
                            occupancyFraction >= 0.6f -> AmberFlame
                            else                      -> DungeonGreen
                        }
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(DungeonNoir500)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(occupancyFraction)
                            .fillMaxHeight()
                            .background(
                                Brush.horizontalGradient(
                                    when {
                                        occupancyFraction >= 0.9f ->
                                            listOf(DragonRed, DragonRed)
                                        occupancyFraction >= 0.6f ->
                                            listOf(AmberFlame700, AmberFlame)
                                        else ->
                                            listOf(DungeonGreen, AncientGold700)
                                    }
                                )
                            )
                    )
                }
            }

            // Botón de entrar
            if (room.isActive) {
                Button(
                    onClick = onClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AmberFlame,
                        contentColor   = InkBlack
                    ),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        "UNIRSE A LA SALA",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════
//  TAB 2: Lista de miembros del gremio
// ═══════════════════════════════════════════════════════════════
@Composable
private fun GuildTab(members: List<GuildMember>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            GuildStatsHeader(members = members)
            Spacer(modifier = Modifier.height(8.dp))
        }

        items(members) { member ->
            MemberCard(member = member)
        }
    }
}

// ── Cabecera de stats del gremio ─────────────────────────────────
@Composable
private fun GuildStatsHeader(members: List<GuildMember>) {
    val studying = members.count { it.status == MemberStatus.STUDYING }
    val totalXp  = members.sumOf { it.totalXp }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(DungeonNoir700)
            .border(
                1.dp,
                Brush.linearGradient(listOf(SaddleBrown, AncientGold700, SaddleBrown)),
                RoundedCornerShape(6.dp)
            )
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = "📜  REGISTRO DEL GREMIO",
                style = MaterialTheme.typography.labelMedium,
                color = AncientGold,
                letterSpacing = 1.sp
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                GuildStat(label = "MIEMBROS",  value = "${members.size}")
                VerticalDivider()
                GuildStat(label = "ACTIVOS",   value = "$studying", highlight = true)
                VerticalDivider()
                GuildStat(label = "XP TOTAL",  value = "$totalXp")
            }
        }
    }
}

@Composable
private fun GuildStat(label: String, value: String, highlight: Boolean = false) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            color = if (highlight) AmberFlame else AncientGold,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = SteelSilver500,
            letterSpacing = 0.8.sp
        )
    }
}

@Composable
private fun VerticalDivider() {
    Box(
        modifier = Modifier
            .width(1.dp)
            .height(40.dp)
            .background(SteelSilver200)
    )
}

// ── Card de miembro del gremio ───────────────────────────────────
@Composable
private fun MemberCard(member: GuildMember) {
    val statusColor = when (member.status) {
        MemberStatus.STUDYING -> AmberFlame
        MemberStatus.PAUSED   -> AncientGold700
        MemberStatus.IDLE     -> SteelSilver500
        MemberStatus.OFFLINE  -> DragonRed
    }
    val statusLabel = when (member.status) {
        MemberStatus.STUDYING -> "ESTUDIANDO"
        MemberStatus.PAUSED   -> "EN PAUSA"
        MemberStatus.IDLE     -> "INACTIVO"
        MemberStatus.OFFLINE  -> "OFFLINE"
    }

    // Dot pulsante para STUDYING
    val pulseAnim = rememberInfiniteTransition(label = "member${member.id}")
    val dotScale by pulseAnim.animateFloat(
        initialValue = 1f, targetValue = 1.4f,
        animationSpec = infiniteRepeatable(tween(800), RepeatMode.Reverse),
        label = "dot${member.id}"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(DungeonNoir700)
            .border(1.dp, SteelSilver200, RoundedCornerShape(6.dp))
            .padding(14.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // Avatar con badge de nivel
            Box(contentAlignment = Alignment.BottomEnd) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(SaddleBrown700)
                        .border(
                            1.5.dp,
                            if (member.status == MemberStatus.STUDYING) AmberFlame else SteelSilver200,
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = member.avatarEmoji, fontSize = 22.sp)
                }
                // Badge de nivel
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(AncientGold)
                        .size(18.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${member.level}",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                        color = InkBlack,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Info del miembro
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = member.name,
                        style = MaterialTheme.typography.titleSmall,
                        color = SteelSilver,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    Text(
                        text = member.role,
                        style = MaterialTheme.typography.labelSmall,
                        color = SaddleBrown300,
                        letterSpacing = 0.5.sp
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Dot de estado
                    Box(
                        modifier = Modifier
                            .size(if (member.status == MemberStatus.STUDYING) 7.dp * dotScale else 7.dp)
                            .clip(CircleShape)
                            .background(statusColor)
                    )
                    Text(
                        text = statusLabel,
                        style = MaterialTheme.typography.labelSmall,
                        color = statusColor
                    )
                    if (member.status == MemberStatus.STUDYING && member.sessionSeconds > 0) {
                        val m = member.sessionSeconds / 60
                        val s = member.sessionSeconds % 60
                        Text(
                            text = "· ${m}m ${s}s",
                            style = MaterialTheme.typography.labelSmall,
                            color = SteelSilver500
                        )
                    }
                }
            }

            // XP total
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${member.totalXp}",
                    style = MaterialTheme.typography.titleSmall,
                    color = AncientGold,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "XP TOTAL",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                    color = SteelSilver500,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════
//  RoomDetailScreen — vista interior de una sala
// ═══════════════════════════════════════════════════════════════
@Composable
private fun RoomDetailScreen(
    room: StudyRoom,
    members: List<GuildMember>,
    onBack: () -> Unit
) {
    val roomEmoji = when (room.roomType) {
        "Mazmorra"       -> "🏚"
        "Torre del Mago" -> "🗼"
        "Biblioteca"     -> "📚"
        else             -> "🏰"
    }

    // Animación de antorcha en el header
    val torchAnim = rememberInfiniteTransition(label = "torch")
    val torchAlpha by torchAnim.animateFloat(
        initialValue = 0.7f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(600, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "torchAlpha"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(DungeonNoir700, DungeonNoir, InkBlack)))
    ) {
        // ── Header de la sala ───────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(DungeonNoir700)
                .border(
                    width = 0.dp,
                    color = Color.Transparent
                )
                .padding(16.dp)
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(onClick = onBack, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Filled.ArrowBack, "Volver", tint = SteelSilver)
                    }
                    Text(
                        text = "$roomEmoji  ${room.name}",
                        style = MaterialTheme.typography.headlineSmall,
                        color = AncientGold
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    // Antorchas decorativas animadas
                    Text(
                        text = "🔥",
                        fontSize = 20.sp,
                        modifier = Modifier.alpha(torchAlpha)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = room.description,
                    style = MaterialTheme.typography.bodySmall.copy(fontStyle = FontStyle.Italic),
                    color = SteelSilver500
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Barra decorativa dorada
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color.Transparent, AncientGold700, AncientGold, AncientGold700, Color.Transparent)
                            )
                        )
                )
            }
        }

        // ── Avatares en LazyRow (vista compacta) ────────────────
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .background(DungeonNoir700)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(members) { member ->
                CompactMemberAvatar(member = member)
            }
        }

        // ── Lista completa de miembros en la sala ───────────────
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Text(
                    text = "AVENTUREROS EN LA SALA",
                    style = MaterialTheme.typography.labelMedium,
                    color = AncientGold700,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
            }

            items(members) { member ->
                MemberCard(member = member)
            }

            item {
                Spacer(modifier = Modifier.height(12.dp))
                // Botón de acción: entrar a estudiar en esta sala
                Button(
                    onClick = { /* TODO: viewModel.joinRoom(room.id) */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AmberFlame,
                        contentColor   = InkBlack
                    ),
                    shape = RoundedCornerShape(4.dp),
                    elevation = ButtonDefaults.buttonElevation(8.dp)
                ) {
                    Text(
                        "⚔  COMENZAR MISIÓN EN ESTA SALA",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

// ── Avatar compacto para LazyRow ────────────────────────────────
@Composable
private fun CompactMemberAvatar(member: GuildMember) {
    val isStudying = member.status == MemberStatus.STUDYING

    val pulseAnim = rememberInfiniteTransition(label = "compact${member.id}")
    val borderAlpha by pulseAnim.animateFloat(
        initialValue = 0.6f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(900), RepeatMode.Reverse),
        label = "cBorder${member.id}"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(SaddleBrown700)
                .border(
                    2.dp,
                    if (isStudying) AmberFlame.copy(alpha = borderAlpha) else SteelSilver200,
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(member.avatarEmoji, fontSize = 24.sp)
        }
        Text(
            text = member.name.split(" ").first(),
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
            color = if (isStudying) AncientGold else SteelSilver500,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
        // Mini barra de tiempo de sesión
        if (member.sessionSeconds > 0) {
            Text(
                text = "${member.sessionSeconds / 60}m",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                color = AmberFlame
            )
        }
    }
}