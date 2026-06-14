package com.example.focus.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// ═══════════════════════════════════════════════════════════════
//  FOCUS MOBILE — Design System · Typography
//
//  INSTRUCCIONES DE FUENTES:
//  1. Descargá "MedievalSharp" de Google Fonts (gratis):
//     https://fonts.google.com/specimen/MedievalSharp
//     → Copiá el .ttf en: app/src/main/res/font/medievalsharp.ttf
//
//  2. Descargá "Cinzel" (títulos alternativos más legibles):
//     https://fonts.google.com/specimen/Cinzel
//     → app/src/main/res/font/cinzel_regular.ttf
//     → app/src/main/res/font/cinzel_bold.ttf
//
//  3. "IM Fell English" para el cuerpo (feel de manuscrito):
//     https://fonts.google.com/specimen/IM+Fell+English
//     → app/src/main/res/font/im_fell_english_regular.ttf
//     → app/src/main/res/font/im_fell_english_italic.ttf
//
//  Si todavía no tenés las fuentes descargadas, cambiá
//  FontFamily.Serif por FontFamily.Default como fallback temporal.
// ═══════════════════════════════════════════════════════════════

// ── Familia de títulos: Cinzel ──────────────────────────────────
// Cinzel tiene serifas inspiradas en la Roma clásica y encaja
// perfectamente con la estética D&D. Más legible que MedievalSharp
// en tamaños pequeños.
val CinzelFamily = FontFamily(
    Font(resId = com.example.focus.R.font.cinzel_regular, weight = FontWeight.Normal),
    Font(resId = com.example.focus.R.font.cinzel_bold,    weight = FontWeight.Bold)
)

// ── Familia de cuerpo: IM Fell English ─────────────────────────
// Simula un texto impreso en una imprenta antigua. Perfecto para
// descripciones de misiones, diálogos, labels de stats.
val ImFellFamily = FontFamily(
    Font(resId = com.example.focus.R.font.im_fell_english_regular, weight = FontWeight.Normal),
    Font(resId = com.example.focus.R.font.im_fell_english_italic,  weight = FontWeight.Normal, style = FontStyle.Italic)
)

// ── Typography de Material 3 ────────────────────────────────────
val FocusTypography = Typography(

    // Títulos de pantalla — "El Gremio te convoca"
    displayLarge = TextStyle(
        fontFamily    = CinzelFamily,
        fontWeight    = FontWeight.Bold,
        fontSize      = 48.sp,
        lineHeight    = 56.sp,
        letterSpacing = 1.5.sp
    ),
    displayMedium = TextStyle(
        fontFamily    = CinzelFamily,
        fontWeight    = FontWeight.Bold,
        fontSize      = 36.sp,
        lineHeight    = 44.sp,
        letterSpacing = 1.sp
    ),
    displaySmall = TextStyle(
        fontFamily    = CinzelFamily,
        fontWeight    = FontWeight.Bold,
        fontSize      = 28.sp,
        lineHeight    = 36.sp,
        letterSpacing = 0.8.sp
    ),

    // Encabezados de sección / nombre de misión
    headlineLarge = TextStyle(
        fontFamily    = CinzelFamily,
        fontWeight    = FontWeight.Bold,
        fontSize      = 26.sp,
        lineHeight    = 34.sp,
        letterSpacing = 0.5.sp
    ),
    headlineMedium = TextStyle(
        fontFamily    = CinzelFamily,
        fontWeight    = FontWeight.Bold,
        fontSize      = 22.sp,
        lineHeight    = 30.sp,
        letterSpacing = 0.3.sp
    ),
    headlineSmall = TextStyle(
        fontFamily    = CinzelFamily,
        fontWeight    = FontWeight.Normal,
        fontSize      = 18.sp,
        lineHeight    = 26.sp,
        letterSpacing = 0.2.sp
    ),

    // Títulos de tarjetas / subtítulos
    titleLarge = TextStyle(
        fontFamily    = CinzelFamily,
        fontWeight    = FontWeight.Bold,
        fontSize      = 18.sp,
        lineHeight    = 24.sp,
        letterSpacing = 0.2.sp
    ),
    titleMedium = TextStyle(
        fontFamily    = ImFellFamily,
        fontWeight    = FontWeight.Bold,
        fontSize      = 16.sp,
        lineHeight    = 22.sp,
        letterSpacing = 0.15.sp
    ),
    titleSmall = TextStyle(
        fontFamily    = ImFellFamily,
        fontWeight    = FontWeight.Bold,
        fontSize      = 14.sp,
        lineHeight    = 20.sp,
        letterSpacing = 0.1.sp
    ),

    // Cuerpo de texto — descripciones de misiones, lore
    bodyLarge = TextStyle(
        fontFamily    = ImFellFamily,
        fontWeight    = FontWeight.Normal,
        fontSize      = 16.sp,
        lineHeight    = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontFamily    = ImFellFamily,
        fontWeight    = FontWeight.Normal,
        fontSize      = 14.sp,
        lineHeight    = 20.sp,
        letterSpacing = 0.25.sp
    ),
    bodySmall = TextStyle(
        fontFamily    = ImFellFamily,
        fontWeight    = FontWeight.Normal,
        fontSize      = 12.sp,
        lineHeight    = 16.sp,
        letterSpacing = 0.4.sp
    ),

    // Labels — stats, badges, chips
    labelLarge = TextStyle(
        fontFamily    = CinzelFamily,
        fontWeight    = FontWeight.Bold,
        fontSize      = 14.sp,
        lineHeight    = 20.sp,
        letterSpacing = 0.8.sp
    ),
    labelMedium = TextStyle(
        fontFamily    = ImFellFamily,
        fontWeight    = FontWeight.Normal,
        fontSize      = 12.sp,
        lineHeight    = 16.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontFamily    = ImFellFamily,
        fontWeight    = FontWeight.Normal,
        fontSize      = 10.sp,
        lineHeight    = 14.sp,
        letterSpacing = 0.5.sp
    )
)