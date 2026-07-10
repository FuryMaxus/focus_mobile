package com.example.focus.ui.component

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.focus.R

// ═══════════════════════════════════════════════════════════════
//  FOCUS MOBILE — Escena 2D Parallax Infinito
// ═══════════════════════════════════════════════════════════════

/**
 * Representa una capa del fondo parallax con su imagen y velocidad.
 */
data class ParallaxLayer(
    val drawableRes: Int,
    val durationMillis: Int
)

@Composable
fun PantallaJuego(
    characterRes: Int = R.drawable.char_duende_verde // Personaje por defecto
) {
    val layers = listOf(
        ParallaxLayer(R.drawable.layer_0011_0, 20000), // El más lento (cielo)
        ParallaxLayer(R.drawable.layer_0010_1, 18000),
        ParallaxLayer(R.drawable.layer_0009_2, 16000),
        ParallaxLayer(R.drawable.layer_0008_3, 14000),
        ParallaxLayer(R.drawable.layer_0007_lights, 14000),
        ParallaxLayer(R.drawable.layer_0006_4, 12000),
        ParallaxLayer(R.drawable.layer_0005_5, 10000),
        ParallaxLayer(R.drawable.layer_0004_lights, 10000),
        ParallaxLayer(R.drawable.layer_0003_6, 8000),
        ParallaxLayer(R.drawable.layer_0002_7, 6000),
        ParallaxLayer(R.drawable.layer_0001_8, 4500),
        ParallaxLayer(R.drawable.layer_0000_9, 3000)  // El más rápido (suelo)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clipToBounds()
    ) {
        // Dibujamos todas las capas del fondo
        layers.forEach { layer ->
            InfiniteParallaxLayer(layer, isRunning = true)
        }

        // Posicionamos al personaje apoyado sobre el suelo (Layer 9) de forma responsiva
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = BiasAlignment(0f, 0.92f) // Usamos BiasAlignment para que sea responsivo al fondo estirado
        ) {
            PersonajeAnimadoCaminando(imagenRes = characterRes)
        }
    }
}

@Composable
fun ParallaxBackground(
    isRunning: Boolean = true,
    intensity: Float = 0f,
    dynamicColor: Color = Color.Unspecified
) {
    val layers = listOf(
        ParallaxLayer(R.drawable.layer_0011_0, 20000),
        ParallaxLayer(R.drawable.layer_0010_1, 18000),
        ParallaxLayer(R.drawable.layer_0009_2, 16000),
        ParallaxLayer(R.drawable.layer_0008_3, 14000),
        ParallaxLayer(R.drawable.layer_0007_lights, 14000),
        ParallaxLayer(R.drawable.layer_0006_4, 12000),
        ParallaxLayer(R.drawable.layer_0005_5, 10000),
        ParallaxLayer(R.drawable.layer_0004_lights, 10000),
        ParallaxLayer(R.drawable.layer_0003_6, 8000),
        ParallaxLayer(R.drawable.layer_0002_7, 6000),
        ParallaxLayer(R.drawable.layer_0001_8, 4500),
        ParallaxLayer(R.drawable.layer_0000_9, 3000)
    )

    Box(modifier = Modifier.fillMaxSize()) {
        layers.forEach { layer ->
            InfiniteParallaxLayer(layer, isRunning)
        }
        
        // Tinte de intensidad si se proporciona
        if (dynamicColor != Color.Unspecified) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(dynamicColor.copy(alpha = 0.1f + (intensity * 0.2f)), Color.Transparent),
                            radius = 2000f
                        )
                    )
            )
        }
    }
}

@Composable
fun InfiniteParallaxLayer(layer: ParallaxLayer, isRunning: Boolean) {
    val transition = rememberInfiniteTransition(label = "parallax")
    
    val offsetProgress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(layer.durationMillis, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "offset"
    )

    val currentOffsetProgress = if (isRunning) offsetProgress else 0f

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val widthPx = with(LocalDensity.current) { maxWidth.toPx() }
        
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    // Cambiamos el signo para que se mueva a la derecha
                    translationX = currentOffsetProgress * widthPx
                }
        ) {
            Image(
                painter = painterResource(id = layer.drawableRes),
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                alignment = Alignment.BottomCenter,
                modifier = Modifier.fillMaxSize()
            )
            // Colocamos la segunda imagen a la IZQUIERDA de la primera
            Image(
                painter = painterResource(id = layer.drawableRes),
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                alignment = Alignment.BottomCenter,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        translationX = -widthPx
                    }
            )
        }
    }
}

/**
 * Función reutilizable para animar personajes caminando con balanceo y rebote.
 */
@Composable
fun PersonajeAnimadoCaminando(imagenRes: Int) {
    val transition = rememberInfiniteTransition(label = "Caminata")

    // 1. Balanceo (Rotación): ±6 grados, 400ms
    val rotation by transition.animateFloat(
        initialValue = -6f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 400, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "balanceo"
    )

    // 2. Rebote (Salto): 0 a -15f, 200ms (doble frecuencia que la rotación)
    val translationY by transition.animateFloat(
        initialValue = 0f,
        targetValue = -15f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "rebote"
    )

    Image(
        painter = painterResource(id = imagenRes),
        contentDescription = "Héroe caminando",
        modifier = Modifier
            .size(250.dp)
            .graphicsLayer {
                rotationZ = rotation
                this.translationY = translationY
            }
    )
}
