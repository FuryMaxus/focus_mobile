package com.example.focus.ui.screen

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.focus.network.RetrofitClient
import com.example.focus.network.SessionItem
import com.example.focus.network.SyncPayload
import com.example.focus.network.TokenManager
import com.example.focus.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.foundation.Canvas

// ═══════════════════════════════════════════════════════════════
//  ClockScreen — Rediseño visual completo con animaciones
//  Lógica de negocio: idéntica al original
// ═══════════════════════════════════════════════════════════════
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClockScreen(navController: NavController) {

    // ── Lógica original intacta ─────────────────────────────────
    val context = LocalContext.current
    val scope   = rememberCoroutineScope()
    var isRunning        by remember { mutableStateOf(false) }
    var seconds          by remember { mutableStateOf(0) }
    var startTime        by remember { mutableStateOf<ZonedDateTime?>(null) }
    var mensajeResultado by remember { mutableStateOf("") }

    LaunchedEffect(isRunning) {
        while (isRunning) {
            delay(1000L)
            seconds++
        }
    }
    // ────────────────────────────────────────────────────────────

    // ── Estado de animación ─────────────────────────────────────
    val isPulsing = isRunning

    // Pulso del anillo exterior — late como un corazón
    val pulseAnim = rememberInfiniteTransition(label = "pulse")
    val pulseScale by pulseAnim.animateFloat(
        initialValue = 1f,
        targetValue  = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    // Rotación lenta del anillo de runas decorativo
    val runeRotation by pulseAnim.animateFloat(
        initialValue = 0f,
        targetValue  = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing)
        ),
        label = "runeRot"
    )

    // Brillo del número (parpadeo muy sutil)
    val glowAlpha by pulseAnim.animateFloat(
        initialValue = 0.6f,
        targetValue  = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )

    // Partículas de fuego flotantes
    val particle1Y by pulseAnim.animateFloat(
        initialValue = 0f, targetValue = -30f,
        animationSpec = infiniteRepeatable(tween(1800, easing = EaseInOut), RepeatMode.Reverse),
        label = "p1"
    )
    val particle2Y by pulseAnim.animateFloat(
        initialValue = 0f, targetValue = -22f,
        animationSpec = infiniteRepeatable(tween(2300, easing = EaseInOut), RepeatMode.Reverse),
        label = "p2"
    )
    val particle3Y by pulseAnim.animateFloat(
        initialValue = 0f, targetValue = -18f,
        animationSpec = infiniteRepeatable(tween(1500, easing = EaseInOut), RepeatMode.Reverse),
        label = "p3"
    )
    val particleAlpha by pulseAnim.animateFloat(
        initialValue = if (isPulsing) 0.3f else 0f,
        targetValue  = if (isPulsing) 0.9f else 0f,
        animationSpec = infiniteRepeatable(tween(1600, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "pAlpha"
    )

    // Progreso del arco SVG (sube cada 60 segundos en ciclo)
    val arcProgress = if (seconds == 0) 0f else ((seconds % 60) / 60f)
    val arcProgressAnim by animateFloatAsState(
        targetValue = arcProgress,
        animationSpec = tween(durationMillis = 800, easing = EaseOutCubic),
        label = "arc"
    )

    // Frases motivacionales que rotan cada 30s
    val quotes = listOf(
        "\"El conocimiento forja héroes.\"",
        "\"Cada minuto es XP ganada.\"",
        "\"La mazmorra recompensa la constancia.\"",
        "\"Estudia como si tu gremio dependiera de ello.\"",
        "\"Los grandes magos empezaron aquí.\""
    )
    var quoteIndex by remember { mutableStateOf(0) }
    LaunchedEffect(isRunning) {
        while (isRunning) {
            delay(30_000L)
            quoteIndex = (quoteIndex + 1) % quotes.size
        }
    }

    // ── UI ──────────────────────────────────────────────────────
    Scaffold(
        containerColor = DungeonNoir,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "SESIÓN DE ESTUDIO",
                            style = MaterialTheme.typography.titleLarge,
                            color = AncientGold
                        )
                        Text(
                            "Misión en curso",
                            style = MaterialTheme.typography.labelSmall,
                            color = SteelSilver500,
                            letterSpacing = 1.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Filled.ArrowBack,
                            contentDescription = "Atrás",
                            tint = SteelSilver
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DungeonNoir700
                )
            )
        }
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(DungeonNoir700, DungeonNoir, InkBlack)
                    )
                )
                .padding(paddingValues)
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {

                // ── Parte superior: franja de estado ───────────
                StatusBadge(isRunning = isRunning, seconds = seconds)

                // ── Centro: reloj con animaciones ───────────────
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.weight(1f)
                ) {
                    // Partículas de fuego (solo cuando corre)
                    if (isRunning) {
                        FireParticles(
                            particle1Y = particle1Y,
                            particle2Y = particle2Y,
                            particle3Y = particle3Y,
                            alpha      = particleAlpha
                        )
                    }

                    // Anillo exterior rotante (runas decorativas)
                    Box(
                        modifier = Modifier
                            .size(280.dp)
                            .rotate(runeRotation)
                            .alpha(if (isRunning) 0.25f else 0.08f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "✦ ᚠ ✦ ᚢ ✦ ᚦ ✦ ᚨ ✦ ᚱ ✦ ᚲ ✦ ᚷ ✦ ᚹ ✦ ᚺ ✦ ᚾ ✦",
                            color = AncientGold,
                            fontSize = 11.sp,
                            letterSpacing = 2.sp,
                            textAlign = TextAlign.Center
                        )
                    }

                    // Anillo de progreso (arco Canvas)
                    Canvas(
                        modifier = Modifier
                            .size(240.dp)
                            .scale(if (isRunning) pulseScale else 1f)
                    ) {
                        val strokeWidth = 6.dp.toPx()
                        val inset = strokeWidth / 2f

                        // Track (fondo del arco)
                        drawArc(
                            color       = DungeonNoir500,
                            startAngle  = -90f,
                            sweepAngle  = 360f,
                            useCenter   = false,
                            topLeft     = Offset(inset, inset),
                            size        = Size(size.width - strokeWidth, size.height - strokeWidth),
                            style       = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )

                        // Progreso en oro
                        if (arcProgressAnim > 0f) {
                            drawArc(
                                brush = Brush.sweepGradient(
                                    colors = listOf(
                                        AncientGold700,
                                        AncientGold,
                                        AncientGold200,
                                        AncientGold
                                    )
                                ),
                                startAngle = -90f,
                                sweepAngle = 360f * arcProgressAnim,
                                useCenter  = false,
                                topLeft    = Offset(inset, inset),
                                size       = Size(size.width - strokeWidth, size.height - strokeWidth),
                                style      = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                            )
                        }
                    }

                    // Círculo interior: fondo del reloj
                    Box(
                        modifier = Modifier
                            .size(210.dp)
                            .clip(CircleShape)
                            .background(DungeonNoir700)
                            .border(
                                width = 1.5.dp,
                                brush = Brush.linearGradient(
                                    colors = listOf(SaddleBrown, AncientGold700, SaddleBrown)
                                ),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {

                            // Etiqueta superior
                            Text(
                                text = if (isRunning) "EN MISIÓN" else if (seconds > 0) "EN PAUSA" else "LISTO",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isRunning) AmberFlame else SteelSilver500,
                                letterSpacing = 2.sp
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            // TIEMPO — número principal
                            val minutesStr = (seconds / 60).toString().padStart(2, '0')
                            val secondsStr = (seconds % 60).toString().padStart(2, '0')

                            Text(
                                text = "$minutesStr:$secondsStr",
                                style = MaterialTheme.typography.displayLarge.copy(
                                    fontSize     = 52.sp,
                                    letterSpacing = 4.sp
                                ),
                                color  = AncientGold.copy(alpha = if (isRunning) glowAlpha else 0.9f),
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            // XP ganada estimada
                            val xpEstimada = (seconds / 60) * 10
                            Text(
                                text = if (xpEstimada > 0) "+$xpEstimada XP" else "· · ·",
                                style = MaterialTheme.typography.labelMedium,
                                color = if (xpEstimada > 0) DungeonGreen else SteelSilver500
                            )
                        }
                    }
                }

                // ── Frase motivacional (rota cada 30s) ─────────
                if (isRunning) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(4.dp))
                            .background(DungeonNoir700)
                            .border(1.dp, SteelSilver200, RoundedCornerShape(4.dp))
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = quotes[quoteIndex],
                            style = MaterialTheme.typography.bodySmall.copy(fontStyle = FontStyle.Italic),
                            color = SteelSilver500,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // ── Botones de control ──────────────────────────
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    if (!isRunning) {
                        // INICIAR / REANUDAR
                        Button(
                            onClick = {
                                isRunning = true
                                startTime = ZonedDateTime.now(ZoneOffset.UTC)
                                mensajeResultado = ""
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AmberFlame,
                                contentColor   = InkBlack
                            ),
                            shape = RoundedCornerShape(4.dp),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
                        ) {
                            Text(
                                text = if (seconds == 0) "⚔  INICIAR MISIÓN" else "▶  REANUDAR MISIÓN",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // TERMINAR (solo si hay tiempo acumulado)
                        if (seconds > 0) {
                            Button(
                                onClick = {
                                    val endTime   = ZonedDateTime.now(ZoneOffset.UTC)
                                    val formatter = DateTimeFormatter.ISO_INSTANT

                                    if (startTime != null && seconds > 0) {
                                        scope.launch {
                                            try {
                                                mensajeResultado = "Guardando sesión..."
                                                val sessionItem = SessionItem(
                                                    activity_type = "FOCUS",
                                                    start_time    = formatter.format(startTime),
                                                    end_time      = formatter.format(endTime)
                                                )
                                                val response = RetrofitClient.apiService.syncSessions(
                                                    SyncPayload(listOf(sessionItem))
                                                )
                                                if (response.isSuccessful) {
                                                    val stats = response.body()
                                                    if (stats != null) {
                                                        TokenManager(context).saveStats(
                                                            stats.current_level,
                                                            stats.total_exp_gained
                                                        )
                                                    }
                                                    mensajeResultado = "¡Sesión guardada! Nivel: ${stats?.current_level} (+${stats?.total_exp_gained} EXP)"
                                                    seconds   = 0
                                                    startTime = null
                                                } else {
                                                    mensajeResultado = "Error del servidor: ${response.code()}"
                                                }
                                            } catch (e: Exception) {
                                                mensajeResultado = "Error de red: ${e.message}"
                                            }
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = DragonRedSurface,
                                    contentColor   = AmberFlame200
                                ),
                                border = androidx.compose.foundation.BorderStroke(1.dp, DragonRed),
                                shape  = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    "🏁  TERMINAR Y GUARDAR",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    } else {
                        // PAUSAR (durante la misión)
                        Button(
                            onClick = { isRunning = false },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = DungeonNoir500,
                                contentColor   = SteelSilver
                            ),
                            border = androidx.compose.foundation.BorderStroke(1.dp, SteelSilver200),
                            shape  = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                "⏸  PAUSAR MISIÓN",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Mensaje de resultado
                    if (mensajeResultado.isNotEmpty()) {
                        val isError = mensajeResultado.startsWith("Error")
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (isError) DragonRedSurface else DungeonNoir700)
                                .border(
                                    1.dp,
                                    if (isError) DragonRed else DungeonGreen,
                                    RoundedCornerShape(4.dp)
                                )
                                .padding(12.dp)
                        ) {
                            Text(
                                text = mensajeResultado,
                                style = MaterialTheme.typography.bodySmall,
                                color = if (isError) AmberFlame200 else AncientGold,
                                textAlign = TextAlign.Center,
                                modifier  = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

// ── Badge de estado en la parte superior ────────────────────────
@Composable
private fun StatusBadge(isRunning: Boolean, seconds: Int) {
    val pulseAnim  = rememberInfiniteTransition(label = "badge")
    val dotAlpha by pulseAnim.animateFloat(
        initialValue = 1f, targetValue = 0.2f,
        animationSpec = infiniteRepeatable(tween(700), RepeatMode.Reverse),
        label = "dot"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .background(DungeonNoir700)
            .border(1.dp, SteelSilver200, RoundedCornerShape(4.dp))
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Dot indicador animado
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            isRunning       -> AmberFlame.copy(alpha = dotAlpha)
                            seconds > 0     -> AncientGold700
                            else            -> SteelSilver200
                        }
                    )
            )
            Text(
                text = when {
                    isRunning   -> "MISIÓN ACTIVA"
                    seconds > 0 -> "EN PAUSA"
                    else        -> "SIN INICIAR"
                },
                style = MaterialTheme.typography.labelMedium,
                color = when {
                    isRunning   -> AmberFlame
                    seconds > 0 -> AncientGold700
                    else        -> SteelSilver500
                }
            )
        }

        // Tiempo en minutos formateado
        Text(
            text = "${seconds / 60} min ${seconds % 60} seg",
            style = MaterialTheme.typography.labelSmall,
            color = SteelSilver500
        )
    }

    Spacer(modifier = Modifier.height(8.dp))
}

// ── Partículas de fuego flotantes ───────────────────────────────
@Composable
private fun FireParticles(
    particle1Y: Float,
    particle2Y: Float,
    particle3Y: Float,
    alpha: Float
) {
    Box(
        modifier = Modifier.size(280.dp),
        contentAlignment = Alignment.Center
    ) {
        // Izquierda
        Box(
            modifier = Modifier
                .offset(x = (-80).dp, y = particle1Y.dp)
                .alpha(alpha)
        ) { Text("🔥", fontSize = 14.sp) }

        // Centro arriba
        Box(
            modifier = Modifier
                .offset(x = 0.dp, y = (particle2Y - 100).dp)
                .alpha(alpha * 0.6f)
        ) { Text("✨", fontSize = 10.sp) }

        // Derecha
        Box(
            modifier = Modifier
                .offset(x = 80.dp, y = particle3Y.dp)
                .alpha(alpha)
        ) { Text("🔥", fontSize = 12.sp) }

        // Sparkles adicionales
        Box(
            modifier = Modifier
                .offset(x = (-40).dp, y = (particle1Y - 80).dp)
                .alpha(alpha * 0.5f)
        ) { Text("✦", fontSize = 8.sp, color = AncientGold) }

        Box(
            modifier = Modifier
                .offset(x = 40.dp, y = (particle3Y - 70).dp)
                .alpha(alpha * 0.5f)
        ) { Text("✦", fontSize = 8.sp, color = AncientGold) }
    }
}