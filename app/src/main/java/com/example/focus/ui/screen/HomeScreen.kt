package com.example.focus.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.focus.network.TokenManager
import com.example.focus.viewmodel.HomeViewModel
import com.example.focus.viewmodel.HomeViewModelFactory
import com.example.focus.ui.theme.*
import com.example.focus.ui.OrnamentalDivider


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {


    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }

    val viewModel: HomeViewModel = viewModel(
        factory = HomeViewModelFactory(tokenManager)
    )

    val nivel      by viewModel.nivel.collectAsState()
    val expActual  by viewModel.expActual.collectAsState()
    val expFaltante by viewModel.expFaltante.collectAsState()
    val progreso   by viewModel.progreso.collectAsState()
    // ────────────────────────────────────────────────────────────

    Scaffold(
        containerColor = DungeonNoir,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "FOCUS MOBILE",
                            style = MaterialTheme.typography.titleLarge,
                            color = AncientGold
                        )
                        Text(
                            text = "Tablón de Misiones",
                            style = MaterialTheme.typography.labelSmall,
                            color = SteelSilver500,
                            letterSpacing = 1.sp
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DungeonNoir700
                ),
                actions = {
                    IconButton(
                        onClick = {
                            viewModel.logout(onLogoutSuccess = { // lógica intacta
                                navController.navigate("menu") {
                                    popUpTo(0) { inclusive = true }
                                }
                            })
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
                onClick = { navController.navigate("clock") }, // lógica intacta
                containerColor = AmberFlame,
                contentColor   = InkBlack,
                shape          = RoundedCornerShape(4.dp),
                icon = {
                    Icon(Icons.Filled.PlayArrow, contentDescription = "Iniciar Misión")
                },
                text = {
                    Text(
                        text = "INICIAR MISIÓN",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 8.dp)
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(DungeonNoir700, DungeonNoir, InkBlack)
                    )
                )
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(8.dp))


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
            OrnamentalDivider()
            Spacer(modifier = Modifier.height(20.dp))

            // ── Card de estadísticas ────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(DungeonNoir700)
                    .border(
                        width = 1.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(SaddleBrown, AncientGold700, SaddleBrown)
                        ),
                        shape = RoundedCornerShape(6.dp)
                    )
            ) {
                Column(modifier = Modifier.padding(20.dp)) {

                    // Header de la card
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(2.dp))
                                .background(SaddleBrown700)
                                .border(1.dp, SaddleBrown, RoundedCornerShape(2.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "📜  PERGAMINO DE ESTADÍSTICAS",
                                style = MaterialTheme.typography.labelSmall,
                                color = AncientGold,
                                letterSpacing = 0.8.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))


                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        StatItem(label = "NIVEL",    value = "$nivel")      // lógica intacta
                        StatItemDivider()
                        StatItem(label = "XP ACTUAL",  value = "$expActual")
                        StatItemDivider()
                        StatItem(label = "FALTAN",   value = "$expFaltante")
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Etiqueta de la barra
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

                    // Barra de XP
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(14.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(DungeonNoir500)
                            .border(1.dp, SteelSilver200, RoundedCornerShape(2.dp))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(progreso.coerceIn(0f, 1f))
                                .fillMaxHeight()
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(AncientGold700, AncientGold, AncientGold200)
                                    )
                                )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ── Card de misión activa (placeholder) ─────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(DungeonNoir700)
                    .border(
                        width = 1.dp,
                        color = SteelSilver200,
                        shape = RoundedCornerShape(6.dp)
                    )
                    .padding(20.dp)
            ) {
                Column {
                    Text(
                        text = "MISIÓN ACTIVA",
                        style = MaterialTheme.typography.labelSmall,
                        color = AmberFlame,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Sin misión en curso",
                        style = MaterialTheme.typography.titleMedium,
                        color = SteelSilver
                    )
                    Text(
                        text = "Pulsa INICIAR MISIÓN para comenzar una sesión de estudio y ganar XP.",
                        style = MaterialTheme.typography.bodySmall.copy(fontStyle = FontStyle.Italic),
                        color = SteelSilver500
                    )
                }
            }
        }
    }
}

// ── StatItem: número grande + label pequeño ─────────────────────
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

// ── Separador vertical entre stats ──────────────────────────────
@Composable
private fun StatItemDivider() {
    Box(
        modifier = Modifier
            .width(1.dp)
            .height(48.dp)
            .background(SteelSilver200)
    )
}