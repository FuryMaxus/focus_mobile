package com.example.focus.ui.screen

import android.content.Context
import android.os.PowerManager
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.focus.ui.theme.*
import com.example.focus.ui.utils.AntiFarmObserver
import com.example.focus.viewmodel.ClockViewModel
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClockScreen(
    onNavigateBack: () -> Unit,
    viewModel: ClockViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val isRunning = state.isRunning
    val seconds = state.timeInSeconds
    val mensajeResultado = state.message


    AntiFarmObserver(
        isRunning = isRunning,
        timeInSeconds = seconds,
        onCheatDetected = { viewModel.finishAndSave() }
    )

    val pulseAnim = rememberInfiniteTransition(label = "pulse")
    val pulseScale by pulseAnim.animateFloat(
        initialValue = 1f, targetValue = 1.04f,
        animationSpec = infiniteRepeatable(tween(900, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "pulseScale"
    )
    val runeRotation by pulseAnim.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(20000, easing = LinearEasing)),
        label = "runeRot"
    )
    val glowAlpha by pulseAnim.animateFloat(
        initialValue = 0.6f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1200, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "glowAlpha"
    )
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
        initialValue = if (isRunning) 0.3f else 0f, targetValue = if (isRunning) 0.9f else 0f,
        animationSpec = infiniteRepeatable(tween(1600, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "pAlpha"
    )

    val arcProgress = if (seconds == 0) 0f else ((seconds % 60) / 60f)
    val arcProgressAnim by animateFloatAsState(
        targetValue = arcProgress,
        animationSpec = tween(durationMillis = 800, easing = EaseOutCubic),
        label = "arc"
    )

    // ── Lógica de Intensidad Dinámica ───────
    // Cada 10 segundos sube un escalón de intensidad (max 100 segundos para test)
    val steps = (seconds / 10).coerceAtMost(10)
    val intensityTarget = steps / 10f
    
    val intensity by animateFloatAsState(
        targetValue = intensityTarget,
        animationSpec = tween(2000, easing = LinearOutSlowInEasing),
        label = "intensity"
    )
    
    // Color dinámico: Ámbar -> Oro -> Rojo -> Púrpura Profundo
    val dynamicGlowColor = when {
        intensity < 0.3f -> lerp(AmberFlame, AncientGold, intensity * 3.3f)
        intensity < 0.7f -> lerp(AncientGold, DragonRed, (intensity - 0.3f) * 2.5f)
        else -> lerp(DragonRed, Color(0xFF6200EA), (intensity - 0.7f) * 3.3f)
    }

    // Efecto de Sacudida
    val shakeAnimState = pulseAnim.animateFloat(
        initialValue = -1.5f,
        targetValue = 1.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = if (seconds > 40) 20 else 40, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shake"
    )

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("SESIÓN DE ESTUDIO", style = MaterialTheme.typography.titleLarge, color = AncientGold)
                        Text("Intensidad: ${(intensity * 100).toInt()}%", style = MaterialTheme.typography.labelSmall, color = dynamicGlowColor, fontWeight = FontWeight.Bold)
                    }
                },
                actions = {
                    if (intensity > 0.4f) {
                        val combo = when {
                            intensity > 0.8f -> "COMBO X3"
                            intensity > 0.5f -> "COMBO X2"
                            else -> ""
                        }
                        Text(
                            text = combo,
                            color = dynamicGlowColor,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(end = 16.dp).guildGlow(color = dynamicGlowColor, radius = 4.dp)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás", tint = SteelSilver)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(InkBlack)
        ) {
            // Fondo dinámico con resplandor radial expandido (Vignette Mágica)
            Canvas(modifier = Modifier.fillMaxSize()) {
                val centerOffset = Offset(size.width / 2, size.height * 0.4f)
                drawRect(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            dynamicGlowColor.copy(alpha = 0.1f + (intensity * 0.3f)),
                            Color.Transparent
                        ),
                        center = centerOffset,
                        radius = size.maxDimension * (0.4f + intensity * 0.6f)
                    )
                )
            }

            if (isRunning) {
                // Partículas que flotan por toda la pantalla
                FullScreenEpicParticles(intensity = intensity, color = dynamicGlowColor)
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues) // Aplicamos el padding aquí dentro para que el TopBar sea transparente sobre el fondo
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                StatusBadge(isRunning = isRunning, seconds = seconds)

                ClockDisplay(
                    modifier = Modifier
                        .weight(1f)
                        .graphicsLayer{
                            val isShaking = isRunning && intensity > 0.2f
                            val factor = 1f + (intensity * 10f)
                            val offsetPx = if (isShaking) (shakeAnimState.value * factor).dp.toPx() else 0f
                            translationX = offsetPx
                            translationY = offsetPx
                        },
                    isRunning = isRunning,
                    seconds = seconds,
                    pulseScale = pulseScale + (intensity * 0.12f), // Pulso mucho más grande
                    runeRotation = runeRotation * (1f + intensity), // Gira más rápido
                    glowAlpha = glowAlpha,
                    arcProgressAnim = arcProgressAnim,
                    particle1Y = particle1Y,
                    particle2Y = particle2Y,
                    particle3Y = particle3Y,
                    particleAlpha = particleAlpha,
                    glowColor = dynamicGlowColor
                )

                QuoteBanner(isRunning = isRunning)

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ClockActionButtons(
                        isRunning = isRunning,
                        onStart = { viewModel.startStopwatch() },
                        onSave = { viewModel.finishAndSave() }
                    )

                    FeedbackMessage(
                        message = mensajeResultado,
                        isError = state.isError
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun ClockDisplay(
    modifier: Modifier = Modifier,
    isRunning: Boolean,
    seconds: Int,
    pulseScale: Float,
    runeRotation: Float,
    glowAlpha: Float,
    arcProgressAnim: Float,
    particle1Y: Float,
    particle2Y: Float,
    particle3Y: Float,
    particleAlpha: Float,
    glowColor: Color = AmberFlame
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        if (isRunning) {
            FireParticles(
                particle1Y = particle1Y,
                particle2Y = particle2Y,
                particle3Y = particle3Y,
                alpha = particleAlpha,
                color = glowColor
            )
        }

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

        Canvas(
            modifier = Modifier
                .size(240.dp)
                .scale(if (isRunning) pulseScale else 1f)
        ) {
            val strokeWidth = 6.dp.toPx()
            val inset = strokeWidth / 2f

            drawArc(
                color = DungeonNoir500,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = Offset(inset, inset),
                size = Size(size.width - strokeWidth, size.height - strokeWidth),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            if (arcProgressAnim > 0f) {
                drawArc(
                    brush = Brush.sweepGradient(
                        colors = listOf(AncientGold700, AncientGold, AncientGold200, AncientGold)
                    ),
                    startAngle = -90f,
                    sweepAngle = 360f * arcProgressAnim,
                    useCenter = false,
                    topLeft = Offset(inset, inset),
                    size = Size(size.width - strokeWidth, size.height - strokeWidth),
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
            }
        }

        Box(
            modifier = Modifier
                .size(210.dp)
                .clip(CircleShape)
                .background(DungeonNoir700)
                .border(
                    width = 1.5.dp,
                    brush = Brush.linearGradient(colors = listOf(SaddleBrown, AncientGold700, SaddleBrown)),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = if (isRunning) "EN MISIÓN" else "LISTO",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isRunning) AmberFlame else SteelSilver500,
                    letterSpacing = 2.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                val minutesStr = (seconds / 60).toString().padStart(2, '0')
                val secondsStr = (seconds % 60).toString().padStart(2, '0')

                Text(
                    text = "$minutesStr:$secondsStr",
                    style = MaterialTheme.typography.displayLarge.copy(fontSize = 52.sp, letterSpacing = 4.sp),
                    color = AncientGold.copy(alpha = if (isRunning) glowAlpha else 0.9f),
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                val xpEstimada = (seconds / 60) * 10
                Text(
                    text = if (xpEstimada > 0) "+$xpEstimada XP" else "· · ·",
                    style = MaterialTheme.typography.labelMedium,
                    color = if (xpEstimada > 0) DungeonGreen else SteelSilver500
                )
            }
        }
    }
}

@Composable
private fun ClockActionButtons(
    isRunning: Boolean,
    onStart: () -> Unit,
    onSave: () -> Unit
) {
    if (!isRunning) {
        Button(
            onClick = onStart,
            modifier = Modifier.fillMaxWidth().height(54.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AmberFlame, contentColor = InkBlack),
            shape = RoundedCornerShape(4.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
        ) {
            Text(
                text = "INICIAR MISIÓN",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )
        }
    } else {
        Button(
            onClick = onSave,
            modifier = Modifier.fillMaxWidth().height(54.dp),
            colors = ButtonDefaults.buttonColors(containerColor = DragonRedSurface, contentColor = AmberFlame200),
            border = androidx.compose.foundation.BorderStroke(1.dp, DragonRed),
            shape = RoundedCornerShape(4.dp)
        ) {
            Text("TERMINAR Y GUARDAR", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
        }}
}

@Composable
private fun QuoteBanner(isRunning: Boolean) {
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
            delay(30_000L.milliseconds)
            quoteIndex = (quoteIndex + 1) % quotes.size
        }
    }

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
}

@Composable
private fun FeedbackMessage(message: String, isError: Boolean) {
    if (message.isNotEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(4.dp))
                .background(if (isError) DragonRedSurface else DungeonNoir700)
                .border(1.dp, if (isError) DragonRed else DungeonGreen, RoundedCornerShape(4.dp))
                .padding(12.dp)
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodySmall,
                color = if (isError) AmberFlame200 else AncientGold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun StatusBadge(isRunning: Boolean, seconds: Int) {
    val pulseAnim = rememberInfiniteTransition(label = "badge")
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
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            isRunning -> AmberFlame.copy(alpha = dotAlpha)
                            seconds > 0 -> AncientGold700
                            else -> SteelSilver200
                        }
                    )
            )
            Text(
                text = if (isRunning) "MISIÓN ACTIVA" else "SIN INICIAR",
                style = MaterialTheme.typography.labelMedium,
                color = if (isRunning) AmberFlame else SteelSilver500
            )
        }

        Text(
            text = "${seconds / 60} min ${seconds % 60} seg",
            style = MaterialTheme.typography.labelSmall,
            color = SteelSilver500
        )
    }
    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
private fun FireParticles(
    particle1Y: Float,
    particle2Y: Float,
    particle3Y: Float,
    alpha: Float,
    color: Color = AmberFlame
) {
    Box(
        modifier = Modifier.size(280.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(modifier = Modifier.offset(x = (-80).dp, y = particle1Y.dp).alpha(alpha)) { Text("🔥", fontSize = 14.sp) }
        Box(modifier = Modifier.offset(x = 0.dp, y = (particle2Y - 100).dp).alpha(alpha * 0.6f)) { Text("✨", fontSize = 10.sp) }
        Box(modifier = Modifier.offset(x = 80.dp, y = particle3Y.dp).alpha(alpha)) { Text("🔥", fontSize = 12.sp) }
        Box(modifier = Modifier.offset(x = (-40).dp, y = (particle1Y - 80).dp).alpha(alpha * 0.5f)) { Text("✦", fontSize = 8.sp, color = color) }
        Box(modifier = Modifier.offset(x = 40.dp, y = (particle3Y - 70).dp).alpha(alpha * 0.5f)) { Text("✦", fontSize = 8.sp, color = color) }
    }
}

@Composable
private fun FullScreenEpicParticles(intensity: Float, color: Color) {
    val transition = rememberInfiniteTransition(label = "epicParticles")
    
    // Generamos varias partículas con diferentes trayectorias
    val p1Offset by transition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(4000, easing = LinearEasing)),
        label = "ep1"
    )
    val p2Offset by transition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(3000, easing = LinearEasing)),
        label = "ep2"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        // Partícula 1: Sube por la izquierda
        Box(modifier = Modifier
            .align(Alignment.BottomStart)
            .offset(x = 40.dp, y = (-(1000 * p1Offset)).dp)
            .alpha((1f - p1Offset) * intensity)
        ) { Text("🔥", fontSize = (14 + intensity * 10).sp) }

        // Partícula 2: Sube por la derecha
        Box(modifier = Modifier
            .align(Alignment.BottomEnd)
            .offset(x = (-40).dp, y = (-(1000 * p2Offset)).dp)
            .alpha((1f - p2Offset) * intensity)
        ) { Text("✨", fontSize = (12 + intensity * 10).sp) }

        // Orbes de poder que aparecen con intensidad alta
        if (intensity > 0.6f) {
            Box(modifier = Modifier
                .align(Alignment.Center)
                .offset(x = (if (p1Offset > 0.5f) 120 else -120).dp, y = (shake((p1Offset * 2000).toInt(), 100)).dp)
                .alpha((intensity - 0.6f) * 2f)
            ) { Text("✦", fontSize = 20.sp, color = color) }
        }
    }
}

private fun shake(time: Int, amplitude: Int): Float {
    return (kotlin.math.sin(time.toDouble() / 50.0) * amplitude).toFloat()
}