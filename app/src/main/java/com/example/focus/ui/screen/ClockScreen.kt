package com.example.focus.ui.screen

import android.content.Context
import android.os.PowerManager
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
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
import com.example.focus.ui.component.*
import com.example.focus.ui.state.TimerMode
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
    val mode = state.mode
    val targetTime = state.targetTimeInSecond
    val seconds = state.timeInSeconds
    val mensajeResultado = state.message

    val characterName by viewModel.selectedCharacter.collectAsState()
    val equippedHat by viewModel.equippedHat.collectAsState()
    val character = GuildCharacter.fromName(characterName)


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

    val arcProgress = if (mode == TimerMode.TIME_TRIAL) {
        if (targetTime == 0) 0f else (seconds.toFloat() / targetTime.toFloat()).coerceIn(0f,1f)
    } else {
        if (seconds == 0) 0f else ((seconds % 60) / 60f)
    }
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
                        Text("Aventura Focus", style = MaterialTheme.typography.titleLarge, color = AncientGold)
                        Text(if (mode == TimerMode.TIME_TRIAL) "CONTRARRELOJ" else "EXPLORACIÓN LIBRE", style = MaterialTheme.typography.labelSmall, color = dynamicGlowColor, fontWeight = FontWeight.Bold)
                    }
                },
                /*
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
                }, */
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
            // Fondo Parallax
            ParallaxBackground(
                isRunning = isRunning,
                intensity = intensity,
                dynamicColor = dynamicGlowColor
            )

            // Personaje posicionado de forma responsiva respecto al fondo (pasto)
            if (isRunning) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = BiasAlignment(0f, 0.92f) // Ajuste para que "pise" el suelo del fondo parallax
                ) {
                    AnimatedCharacter(
                        character = character,
                        size = 180.dp,
                        pose = CharacterPose.Walk,
                        hat = equippedHat
                    )
                }
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
                verticalArrangement = Arrangement.Top // Cambiado a Top para subir el reloj
            ) {
                StatusBadge(isRunning = isRunning, mode = mode)

                Spacer(modifier = Modifier.height(16.dp)) // Espacio tras el badge

                ClockDisplay(
                    modifier = Modifier
                        .heightIn(max = 300.dp) // Limitamos altura para que no empuje todo
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
                    glowColor = dynamicGlowColor,
                    mode = mode,
                    targetTime = targetTime
                )

                Spacer(modifier = Modifier.height(20.dp)) // Espacio tras el reloj

                // Movemos los botones de acción aquí para que estén más arriba y no tapen al personaje
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
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

                QuoteBanner(isRunning = isRunning)

                if (!isRunning && seconds == 0) {
                    ModeSelectionUI(
                        currentMode = mode,
                        targetTime = targetTime,
                        onModeSelected = { viewModel.setMode(it) },
                        onTimeSelected = { viewModel.setTargetTime(it) }
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
    isRunning: Boolean, seconds: Int, mode: TimerMode, targetTime: Int,
    pulseScale: Float, runeRotation: Float, glowAlpha: Float, arcProgressAnim: Float,
    particle1Y: Float, particle2Y: Float, particle3Y: Float, particleAlpha: Float, glowColor: Color
) {
    BoxWithConstraints(contentAlignment = Alignment.Center, modifier = modifier) {
        val maxClockSize = 280f
        val boxDimension = minOf(maxWidth,maxHeight).coerceAtMost(maxClockSize.dp)
        val scaleFactor= (boxDimension.value / maxClockSize).coerceIn(0.4f,1.0f)
        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(boxDimension)) {
            if (isRunning) FireParticles(particle1Y, particle2Y, particle3Y, particleAlpha, glowColor, scaleFactor)

            Box(modifier = Modifier.fillMaxSize().rotate(runeRotation).alpha(if (isRunning) 0.25f else 0.08f), contentAlignment = Alignment.Center) {
                Text("✦ ᚠ ✦ ᚢ ✦ ᚦ ✦ ᚨ ✦ ᚱ ✦ ᚲ ✦ ᚷ ✦ ᚹ ✦ ᚺ ✦ ᚾ ✦", color = AncientGold, fontSize = (11*scaleFactor).sp, letterSpacing = (2*scaleFactor).sp, textAlign = TextAlign.Center)
            }

            Canvas(modifier = Modifier.fillMaxSize(0.85f).scale(if (isRunning) pulseScale else 1f)) {
                val strokeWidth = (6*scaleFactor).dp.toPx()
                val inset = strokeWidth / 2f
                drawArc(color = DungeonNoir500, startAngle = -90f, sweepAngle = 360f, useCenter = false, topLeft = Offset(inset, inset), size = Size(size.width - strokeWidth, size.height - strokeWidth), style = Stroke(width = strokeWidth, cap = StrokeCap.Round))
                if (arcProgressAnim > 0f) {
                    drawArc(brush = Brush.sweepGradient(colors = listOf(AncientGold700, AncientGold, AncientGold200, AncientGold)), startAngle = -90f, sweepAngle = 360f * arcProgressAnim, useCenter = false, topLeft = Offset(inset, inset), size = Size(size.width - strokeWidth, size.height - strokeWidth), style = Stroke(width = strokeWidth, cap = StrokeCap.Round))
                }
            }

            Box(
                modifier = Modifier.fillMaxSize(0.75f).clip(CircleShape).background(DungeonNoir700).border((1.5*scaleFactor).dp, Brush.linearGradient(listOf(SaddleBrown, AncientGold700, SaddleBrown)), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(if (isRunning) "EN MISIÓN" else "LISTO", style = MaterialTheme.typography.labelSmall, fontSize = (11*scaleFactor).sp, color = if (isRunning) AmberFlame else SteelSilver500, letterSpacing = (2*scaleFactor).sp)
                    Spacer(modifier = Modifier.height((4*scaleFactor).dp))

                    val displaySeconds = if (mode == TimerMode.TIME_TRIAL) {
                        (targetTime - seconds).coerceAtLeast(0)
                    } else {
                        seconds
                    }

                    val minutesStr = (displaySeconds / 60).toString().padStart(2, '0')
                    val secondsStr = (displaySeconds % 60).toString().padStart(2, '0')

                    Text("$minutesStr:$secondsStr", style = MaterialTheme.typography.displayLarge.copy(fontSize = (46*scaleFactor).sp, letterSpacing = (4*scaleFactor).sp), color = AncientGold.copy(alpha = if (isRunning) glowAlpha else 0.9f), fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height((4*scaleFactor).dp))

                    val xpEstimada = if (mode == TimerMode.TIME_TRIAL) (targetTime / 60) * 15 else (seconds / 60) * 10
                    Text(if (xpEstimada > 0) "+$xpEstimada XP (Estimado)" else "· · ·", style = MaterialTheme.typography.labelMedium, fontSize=(12*scaleFactor).sp, color = if (xpEstimada > 0) DungeonGreen else SteelSilver500)
                }
            }
        }
    }

}

@Composable
fun ModeSelectionUI(
    currentMode: TimerMode,
    targetTime: Int,
    onModeSelected: (TimerMode) -> Unit,
    onTimeSelected: (Int) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(DungeonNoir700).border(1.dp, SteelSilver200, RoundedCornerShape(8.dp)),
            horizontalArrangement = Arrangement.Center
        ) {
            ModeTab(title = "Libre", isSelected = currentMode == TimerMode.NORMAL, onClick = { onModeSelected(TimerMode.NORMAL) })
            ModeTab(title = "Contrarreloj", isSelected = currentMode == TimerMode.TIME_TRIAL, onClick = { onModeSelected(TimerMode.TIME_TRIAL) })
        }

        if (currentMode == TimerMode.TIME_TRIAL) {
            Spacer(modifier = Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(15, 25, 45, 60).forEach { min ->
                    val isSelected = targetTime == (min * 60)
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(if (isSelected) AmberFlame else DungeonNoir700)
                            .border(1.dp, if (isSelected) AmberFlame else SteelSilver200, CircleShape)
                            .clickable { onTimeSelected(min) }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(text = "${min}m", color = if (isSelected) InkBlack else SteelSilver, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun ModeTab(title: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .background(if (isSelected) AncientGold700 else Color.Transparent)
            .clickable { onClick() }
            .padding(horizontal = 24.dp, vertical = 10.dp)
    ) {
        Text(text = title, color = if (isSelected) InkBlack else SteelSilver500, fontWeight = FontWeight.Bold, fontSize = 14.sp)
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
private fun StatusBadge(isRunning: Boolean, mode: TimerMode) {
    Row(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(4.dp)).background(DungeonNoir700).border(1.dp, SteelSilver200, RoundedCornerShape(4.dp)).padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(if (isRunning) AmberFlame else SteelSilver200))
            Text(if (isRunning) "MISIÓN ACTIVA" else "SIN INICIAR", style = MaterialTheme.typography.labelMedium, color = if (isRunning) AmberFlame else SteelSilver500)
        }
        Text(if (mode == TimerMode.TIME_TRIAL) "OBJETIVO FIJO" else "TIEMPO LIBRE", style = MaterialTheme.typography.labelSmall, color = SteelSilver500)
    }
    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
private fun FireParticles(
    particle1Y: Float,
    particle2Y: Float,
    particle3Y: Float,
    alpha: Float,
    color: Color = AmberFlame,
    scale: Float
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Box(modifier = Modifier.offset(x = (-80 *scale).dp, y = (particle1Y*scale).dp).alpha(alpha)) { Text("🔥", fontSize = (14*scale).sp) }
        Box(modifier = Modifier.offset(x = (0*scale).dp, y = ((particle2Y - 100)*scale).dp).alpha(alpha * 0.6f)) { Text("✨", fontSize = (10*scale).sp) }
        Box(modifier = Modifier.offset(x = (80*scale).dp, y = (particle3Y*scale).dp).alpha(alpha)) { Text("🔥", fontSize = (12*scale).sp) }
        Box(modifier = Modifier.offset(x = (-40*scale).dp, y = ((particle1Y - 80)*scale).dp).alpha(alpha * 0.5f)) { Text("✦", fontSize = (8*scale).sp, color = color) }
        Box(modifier = Modifier.offset(x = (40*scale).dp, y = ((particle3Y - 70)*scale).dp).alpha(alpha * 0.5f)) { Text("✦", fontSize = (8*scale).sp, color = color) }
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