package com.example.focus.ui.screen

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.focus.ui.component.GuildCard
import com.example.focus.ui.component.GuildDivider
import com.example.focus.ui.component.RarityBadge
import com.example.focus.ui.component.SectionLabel
import com.example.focus.ui.theme.*
import com.example.focus.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToAuthEntry: () -> Unit,
    onNavigateToClock: () -> Unit,
    onNavigateToInventory: () -> Unit
) {
    val nivel by viewModel.nivel.collectAsState()
    val expActual by viewModel.expActual.collectAsState()
    val expFaltante by viewModel.expFaltante.collectAsState()
    val progreso by viewModel.progreso.collectAsState()

    Scaffold(
        containerColor = DungeonNoir,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "FOCUS",
                            style = MaterialTheme.typography.titleLarge,
                            color = AncientGold,
                            letterSpacing = 2.sp
                        )
                        Text(
                            text = "TABLÓN DE MISIONES",
                            style = MaterialTheme.typography.labelSmall,
                            color = SteelSilver500,
                            letterSpacing = 2.sp
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DungeonNoir700
                ),
                actions = {
                    IconButton(onClick = onNavigateToInventory) {
                        Icon(
                            imageVector = Icons.Filled.Inventory,
                            contentDescription = "Inventario",
                            tint = AncientGold
                        )
                    }
                    IconButton(
                        onClick = {
                            viewModel.logout(onLogoutSuccess = { onNavigateToAuthEntry() })
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Cerrar Sesión",
                            tint = AmberFlame
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { onNavigateToClock() },
                containerColor = AmberFlame,
                contentColor = InkBlack,
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.guildGlow(
                    color = AmberFlame,
                    radius = 20.dp,
                    shape = RoundedCornerShape(6.dp),
                    alpha = 0.6f
                ),
                icon = { Icon(Icons.Filled.PlayArrow, contentDescription = "Iniciar Misión") },
                text = {
                    Text(
                        text = "INICIAR MISIÓN",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 0.dp)
            )
        }
    ) { paddingValues ->
        DungeonBackground {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "¡Aventura a la vista!",
                    style = MaterialTheme.typography.headlineMedium,
                    color = AncientGold,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Prepara tu equipamiento, la mazmorra te aguarda.",
                    style = MaterialTheme.typography.bodyMedium.copy(fontStyle = FontStyle.Italic),
                    color = SteelSilver500,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                GuildDivider(modifier = Modifier.fillMaxWidth(0.8f))
                Spacer(modifier = Modifier.height(20.dp))
                GuildCard(
                    modifier = Modifier.fillMaxWidth(),
                    glowColor = AncientGold700,
                    animatedBorder = true
                ) {
                    Column {
                        SectionLabel("PERGAMINO DE ESTADÍSTICAS")
                        Spacer(modifier = Modifier.height(20.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            StatItem(label = "NIVEL", value = "$nivel")
                            StatItemDivider()
                            StatItem(label = "XP ACTUAL", value = "$expActual")
                            StatItemDivider()
                            StatItem(label = "FALTAN", value = "$expFaltante")
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Progreso al Nivel ${nivel + 1}",
                                style = MaterialTheme.typography.labelMedium,
                                color = SteelSilver500
                            )
                            Text(
                                text = "${(progreso * 100).toInt()}%",
                                style = MaterialTheme.typography.labelMedium,
                                color = AncientGold,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        XpBar(progreso = progreso)
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
                GuildCard(
                    modifier = Modifier.fillMaxWidth(),
                    glow = false
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            SectionLabel("MISIÓN ACTIVA", color = AmberFlame)
                            RarityBadge(text = "EN ESPERA", accent = SteelSilver500, fill = DungeonNoir500)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Sin misión en curso",
                            style = MaterialTheme.typography.titleMedium,
                            color = SteelSilver
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Pulsa INICIAR MISIÓN para comenzar una sesión de estudio y ganar XP.",
                            style = MaterialTheme.typography.bodySmall.copy(fontStyle = FontStyle.Italic),
                            color = SteelSilver500
                        )
                    }
                }
                Spacer(modifier = Modifier.height(96.dp))
            }
        }
    }
}

@Composable
private fun XpBar(progreso: Float) {
    val target = progreso.coerceIn(0f, 1f)
    val animated by animateFloatAsState(
        targetValue = target,
        animationSpec = tween(900, easing = EaseInOutSine),
        label = "xpFill"
    )
    val shimmer = rememberInfiniteTransition(label = "xpShimmer")
    val glowAlpha by shimmer.animateFloat(
        initialValue = 0.6f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1400, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "xpGlow"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(16.dp)
            .clip(RoundedCornerShape(3.dp))
            .background(DungeonNoir500)
            .border(1.dp, SteelSilver200, RoundedCornerShape(3.dp))
    ) {
        if (animated > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animated)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(3.dp))
                    .guildGlow(
                        color = AncientGold,
                        radius = 8.dp,
                        shape = RoundedCornerShape(3.dp),
                        alpha = glowAlpha * 0.7f
                    )
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(AncientGold700, AncientGold, AncientGold200)
                        )
                    )
            )
        }
    }
}

@Composable
fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = AncientGold
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = SteelSilver500,
            letterSpacing = 0.8.sp
        )
    }
}

@Composable
private fun StatItemDivider() {
    Box(
        modifier = Modifier
            .width(1.dp)
            .height(48.dp)
            .background(
                Brush.verticalGradient(
                    listOf(Color.Transparent, SaddleBrown, Color.Transparent)
                )
            )
    )
}
