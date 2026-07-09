package com.example.focus.ui.component

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.focus.R

/** Catálogo de accesorios (gorros) disponibles. */
enum class GuildHat(
    @DrawableRes val resId: Int,
    val displayName: String
) {
    Ninguno(0, "Sin Accesorio"),
    Mago(R.drawable.hat_mago, "Sombrero de Archimago");

    companion object {
        fun fromName(name: String?): GuildHat = entries.firstOrNull { it.name == name } ?: Ninguno
    }
}

/** Catálogo de personajes disponibles como drawables del app. */
enum class GuildCharacter(
    @param:DrawableRes val resId: Int,
    val displayName: String,
    val hatOffsetX: Float = 0f,
    val hatOffsetY: Float = -0.35f
) {
    Duende(R.drawable.char_duende_verde, "Duende Verde", hatOffsetX = 0.073f, hatOffsetY = -0.313f),
    Demonio(R.drawable.char_demonio_morado, "Demonio Morado", hatOffsetX = 0.073f, hatOffsetY = -0.313f),
    Dragon(R.drawable.char_dragon_rojo, "Dragón Rojo", hatOffsetX = -0.026f, hatOffsetY = -0.283f),
    Cavernicola(R.drawable.char_cavernicola, "Cavernícola", hatOffsetX = 0.073f, hatOffsetY = -0.313f);

    companion object {
        fun fromName(name: String?): GuildCharacter =
            entries.firstOrNull { it.name == name } ?: Duende
    }
}

enum class CharacterPose { Idle, Walk }

@Composable
fun AnimatedCharacter(
    character: GuildCharacter,
    modifier: Modifier = Modifier,
    size: Dp = 96.dp,
    pose: CharacterPose = CharacterPose.Idle,
    stroll: Boolean = false,
    hat: GuildHat = GuildHat.Ninguno,
    customHatOffset: Offset? = null
) {
    when (pose) {
        CharacterPose.Idle -> IdleCharacter(character, modifier, size, hat, customHatOffset)
        CharacterPose.Walk -> WalkingCharacter(character, modifier, size, stroll, hat)
    }
}

@Composable
fun IdleCharacter(
    character: GuildCharacter,
    modifier: Modifier = Modifier,
    size: Dp = 96.dp,
    hat: GuildHat = GuildHat.Ninguno,
    customHatOffset: Offset? = null
) {
    val transition = rememberInfiniteTransition(label = "idle")
    val sway by transition.animateFloat(
        initialValue = -2.2f, targetValue = 2.2f,
        animationSpec = infiniteRepeatable(tween(1500, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "sway"
    )
    val bob by transition.animateFloat(
        initialValue = 0f, targetValue = -7f,
        animationSpec = infiniteRepeatable(tween(1500, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "idleBob"
    )
    val breathe by transition.animateFloat(
        initialValue = -1f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1500, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "breathe"
    )

    Box(modifier = modifier.size(size), contentAlignment = Alignment.TopCenter) {
        Image(
            painter = painterResource(id = character.resId),
            contentDescription = character.displayName,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxSize()
                .offset(y = bob.dp)
                .graphicsLayer {
                    rotationZ = sway
                    scaleX = 1f + 0.03f * breathe
                    scaleY = 1f - 0.03f * breathe
                }
        )

        if (hat != GuildHat.Ninguno) {
            val finalX = customHatOffset?.x?.dp ?: (size.value * character.hatOffsetX).dp
            val finalY = customHatOffset?.y?.dp ?: (bob + (size.value * character.hatOffsetY)).dp

            Image(
                painter = painterResource(id = hat.resId),
                contentDescription = hat.displayName,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(size * 0.65f)
                    .offset(x = finalX, y = finalY)
                    .graphicsLayer {
                        rotationZ = sway
                    }
            )
        }
    }
}

@Composable
fun PersonajeAnimado(@DrawableRes imagenRes: Int) {
    val transition = rememberInfiniteTransition(label = "CaminataPersonaje")

    val rotation by transition.animateFloat(
        initialValue = -6f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 400, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "RotacionBalanceo"
    )

    val translationY by transition.animateFloat(
        initialValue = 0f,
        targetValue = -15f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "RebotePaso"
    )

    Image(
        painter = painterResource(id = imagenRes),
        contentDescription = "Personaje animado caminando",
        modifier = Modifier
            .size(250.dp)
            .graphicsLayer {
                this.rotationZ = rotation
                this.translationY = translationY
            }
    )
}

@Composable
fun WalkingCharacter(
    character: GuildCharacter,
    modifier: Modifier = Modifier,
    size: Dp = 96.dp,
    stroll: Boolean = false,
    hat: GuildHat = GuildHat.Ninguno
) {
    val transition = rememberInfiniteTransition(label = "walk")

    val rotation by transition.animateFloat(
        initialValue = -6f, targetValue = 6f,
        animationSpec = infiniteRepeatable(tween(400, easing = LinearOutSlowInEasing), RepeatMode.Reverse),
        label = "rotation"
    )
    val bob by transition.animateFloat(
        initialValue = 0f, targetValue = -15f,
        animationSpec = infiniteRepeatable(tween(200, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "bob"
    )

    val painter = painterResource(id = character.resId)

    if (stroll) {
        val phase by transition.animateFloat(
            initialValue = 0f, targetValue = 1f,
            animationSpec = infiniteRepeatable(tween(9000, easing = LinearEasing), RepeatMode.Restart),
            label = "stroll"
        )
        val goingRight = phase < 0.5f
        val travel = if (goingRight) phase * 2f else (1f - phase) * 2f
        val bias = travel * 2f - 1f

        Box(modifier = modifier.fillMaxWidth().height(size + 16.dp)) {
            Box(
                modifier = Modifier
                    .align(BiasAlignment(horizontalBias = bias, verticalBias = 1f))
                    .size(size)
            ) {
                Image(
                    painter = painter,
                    contentDescription = character.displayName,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize().graphicsLayer {
                        rotationZ = rotation
                        translationY = bob
                        scaleX = if (goingRight) 1f else -1f
                    }
                )
                
                if (hat != GuildHat.Ninguno) {
                    val finalX = (size.value * character.hatOffsetX).dp
                    val finalY = (bob + (size.value * character.hatOffsetY)).dp
                    
                    Image(
                        painter = painterResource(id = hat.resId),
                        contentDescription = hat.displayName,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .size(size * 0.65f)
                            .offset(x = finalX, y = finalY)
                            .graphicsLayer {
                                rotationZ = rotation
                                scaleX = if (goingRight) 1f else -1f
                            }
                    )
                }
            }
        }
    } else {
        Box(modifier = modifier.size(size), contentAlignment = Alignment.TopCenter) {
            Image(
                painter = painter,
                contentDescription = character.displayName,
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize().graphicsLayer { 
                    rotationZ = rotation
                    this.translationY = bob
                }
            )

            if (hat != GuildHat.Ninguno) {
                val finalX = (size.value * character.hatOffsetX).dp
                val finalY = (bob + (size.value * character.hatOffsetY)).dp

                Image(
                    painter = painterResource(id = hat.resId),
                    contentDescription = hat.displayName,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(size * 0.65f)
                        .offset(x = finalX, y = finalY)
                        .graphicsLayer {
                            rotationZ = rotation
                        }
                )
            }
        }
    }
}
