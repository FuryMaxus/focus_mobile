package com.example.focus.ui.component

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.focus.R

// ═══════════════════════════════════════════════════════════════
//  FOCUS MOBILE — Personajes animados (sin Lottie)
//
//  Reproduce en Compose puro los movimientos de los *_idle.json y
//  *_walk.json originales, partiendo de la PNG de frente:
//   · IDLE → balanceo suave ±2.2°, rebote leve y "respiración"
//            (squash/stretch ±3%) en ~3s.
//   · WALK → balanceo ±6.5° + rebote marcado (pasos) en ~2.1s,
//            con desplazamiento de lado a lado opcional (stroll).
// ═══════════════════════════════════════════════════════════════

/** Catálogo de personajes disponibles como drawables del app. */
enum class GuildCharacter(
    @param:DrawableRes val resId: Int,
    val displayName: String
) {
    Duende(R.drawable.char_duende_verde, "Duende Verde"),
    Demonio(R.drawable.char_demonio_morado, "Demonio Morado"),
    Dragon(R.drawable.char_dragon_rojo, "Dragón Rojo"),
    Cavernicola(R.drawable.char_cavernicola, "Cavernícola");

    companion object {
        /** Resuelve por nombre de enum; cae a [Duende] si no coincide. */
        fun fromName(name: String?): GuildCharacter =
            entries.firstOrNull { it.name == name } ?: Duende
    }
}

/** Pose de animación del personaje. */
enum class CharacterPose { Idle, Walk }

/**
 * Personaje animado según [pose].
 *
 * @param stroll solo aplica a [CharacterPose.Walk]: recorre el ancho
 *               disponible y voltea al llegar a cada extremo.
 */
@Composable
fun AnimatedCharacter(
    character: GuildCharacter,
    modifier: Modifier = Modifier,
    size: Dp = 96.dp,
    pose: CharacterPose = CharacterPose.Idle,
    stroll: Boolean = false
) {
    when (pose) {
        CharacterPose.Idle -> IdleCharacter(character, modifier, size)
        CharacterPose.Walk -> WalkingCharacter(character, modifier, size, stroll)
    }
}

/** Personaje en reposo: balanceo suave + respiración (idle). */
@Composable
fun IdleCharacter(
    character: GuildCharacter,
    modifier: Modifier = Modifier,
    size: Dp = 96.dp
) {
    val transition = rememberInfiniteTransition(label = "idle")
    val sway by transition.animateFloat(
        initialValue = -2.2f,
        targetValue = 2.2f,
        animationSpec = infiniteRepeatable(tween(1500, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "sway"
    )
    val bob by transition.animateFloat(
        initialValue = 0f,
        targetValue = -7f,
        animationSpec = infiniteRepeatable(tween(1500, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "idleBob"
    )
    // Respiración: scaleX y scaleY oscilan en contrafase (squash/stretch).
    val breathe by transition.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1500, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "breathe"
    )

    Image(
        painter = painterResource(id = character.resId),
        contentDescription = character.displayName,
        contentScale = ContentScale.Fit,
        modifier = modifier
            .size(size)
            .offset(y = bob.dp)
            .graphicsLayer {
                rotationZ = sway
                scaleX = 1f + 0.03f * breathe
                scaleY = 1f - 0.03f * breathe
            }
    )
}

/**
 * Personaje que "camina".
 *
 * @param stroll si true, además se desplaza de lado a lado y voltea.
 */
@Composable
fun WalkingCharacter(
    character: GuildCharacter,
    modifier: Modifier = Modifier,
    size: Dp = 96.dp,
    stroll: Boolean = false
) {
    val transition = rememberInfiniteTransition(label = "walk")

    // Balanceo del cuerpo: ±6.5°, ciclo completo ~2.13s (como el walk.json)
    val rotation by transition.animateFloat(
        initialValue = -6.5f,
        targetValue = 6.5f,
        animationSpec = infiniteRepeatable(tween(1066, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "rotation"
    )
    // Rebote vertical: dos "pasos" por cada balanceo
    val bob by transition.animateFloat(
        initialValue = 0f,
        targetValue = -10f,
        animationSpec = infiniteRepeatable(tween(533, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "bob"
    )

    val painter = painterResource(id = character.resId)

    if (stroll) {
        // Recorrido de ida y vuelta a lo ancho del contenedor
        val phase by transition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(tween(9000, easing = LinearEasing), RepeatMode.Restart),
            label = "stroll"
        )
        val goingRight = phase < 0.5f
        val travel = if (goingRight) phase * 2f else (1f - phase) * 2f   // 0..1
        val bias = travel * 2f - 1f                                      // -1 (izq) .. +1 (der)

        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(size + 16.dp)
        ) {
            Image(
                painter = painter,
                contentDescription = character.displayName,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .align(BiasAlignment(horizontalBias = bias, verticalBias = 1f))
                    .size(size)
                    .offset(y = bob.dp)
                    .graphicsLayer {
                        rotationZ = rotation
                        scaleX = if (goingRight) 1f else -1f   // voltea según dirección
                    }
            )
        }
    } else {
        Image(
            painter = painter,
            contentDescription = character.displayName,
            contentScale = ContentScale.Fit,
            modifier = modifier
                .size(size)
                .offset(y = bob.dp)
                .graphicsLayer { rotationZ = rotation }
        )
    }
}
