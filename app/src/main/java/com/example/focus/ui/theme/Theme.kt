package com.example.focus.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp


// ═══════════════════════════════════════════════════════════════
//  FOCUS MOBILE — Design System · Theme
//
//  Uso:
//    En tu Activity o NavHost, envolvé con:
//      GremioFocusTheme { MainScreen() }
//
//  Mapeo semántico Material3 → tokens de FocusApp:
//    primary           → AmberFlame        (botones principales)
//    onPrimary         → DungeonNoir        (texto sobre botón)
//    primaryContainer  → AmberFlame700      (variante pressed)
//    secondary         → AncientGold        (acentos, FAB)
//    onSecondary       → InkBlack           (texto sobre oro)
//    secondaryContainer→ AncientGold700     (variante dim)
//    tertiary          → RuneViolet         (rareza mágica)
//    background        → DungeonNoir        (fondo principal)
//    surface           → DungeonNoir700     (tarjetas)
//    surfaceVariant    → DungeonNoir500     (inputs, chips)
//    onBackground      → SteelSilver        (texto principal)
//    onSurface         → SteelSilver        (texto en tarjetas)
//    onSurfaceVariant  → SteelSilver500     (placeholders)
//    outline           → SteelSilver200     (bordes inactivos)
//    outlineVariant    → SaddleBrown        (bordes artefacto)
//    error             → DragonRed          (errores)
//    errorContainer    → DragonRedSurface   (fondo de error)
//    scrim             → Scrim              (overlay modales)
// ═══════════════════════════════════════════════════════════════

private val GremioFocusDarkColorScheme = darkColorScheme(
    // Primario — Amber Flame (acción principal)
    primary              = AmberFlame,
    onPrimary            = DungeonNoir,
    primaryContainer     = AmberFlame700,
    onPrimaryContainer   = AmberFlame200,

    // Secundario — Ancient Gold (acentos, FAB, títulos destacados)
    secondary            = AncientGold,
    onSecondary          = InkBlack,
    secondaryContainer   = AncientGold700,
    onSecondaryContainer = AncientGold200,

    // Terciario — Rune Violet (rareza mágica, badges especiales)
    tertiary             = RuneViolet,
    onTertiary           = PureWhite,
    tertiaryContainer    = RuneViolet,
    onTertiaryContainer  = PureWhite,

    // Fondos
    background           = DungeonNoir,
    onBackground         = SteelSilver,

    // Superficies (tarjetas, bottom sheets, dialogs)
    surface              = DungeonNoir700,
    onSurface            = SteelSilver,
    surfaceVariant       = DungeonNoir500,
    onSurfaceVariant     = SteelSilver500,

    // Bordes
    outline              = SteelSilver200,
    outlineVariant       = SaddleBrown,

    // Errores
    error                = DragonRed,
    onError              = PureWhite,
    errorContainer       = DragonRedSurface,
    onErrorContainer     = AmberFlame200,

    // Otros
    inverseSurface       = SteelSilver,
    inverseOnSurface     = DungeonNoir,
    inversePrimary       = AmberFlame700,
    scrim                = Scrim
)

// ── Shapes: esquinas menos redondeadas = más artefacto, menos app ──
val GremioFocusShapes = Shapes(
    // Chips, badges pequeños
    extraSmall = RoundedCornerShape(2.dp),
    // Inputs, botones
    small      = RoundedCornerShape(4.dp),
    // Tarjetas de misión / stats
    medium     = RoundedCornerShape(6.dp),
    // Bottom sheets, modales
    large      = RoundedCornerShape(8.dp),
    // Pantallas full-sheet
    extraLarge = RoundedCornerShape(12.dp)
)

// ── Composable principal del tema ──────────────────────────────
@Composable
fun GremioFocusTheme(
    // Siempre modo oscuro — la estética no tiene modo claro
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = GremioFocusDarkColorScheme,
        typography  = FocusTypography,
        shapes      = GremioFocusShapes,
        content     = content
    )
}