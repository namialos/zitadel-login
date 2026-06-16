package org.example.zitadellogin.core.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

private val PersianGreen = Color(0xFF0F766E)
private val PersianGreenDark = Color(0xFF115E59)
private val Sand = Color(0xFFF8FAFC)
private val Ink = Color(0xFF0F172A)

val ZitadelLightColors = lightColorScheme(
    primary = PersianGreen,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFCCFBF1),
    onPrimaryContainer = Ink,
    secondary = PersianGreenDark,
    onSecondary = Color.White,
    tertiary = Color(0xFF6366F1),
    background = Sand,
    surface = Color.White,
    onBackground = Ink,
    onSurface = Ink,
    error = Color(0xFFB91C1C),
    onError = Color.White,
)

val ZitadelDarkColors = darkColorScheme(
    primary = Color(0xFF2DD4BF),
    onPrimary = Ink,
    primaryContainer = PersianGreenDark,
    onPrimaryContainer = Color(0xFFECFDF5),
    secondary = Color(0xFF5EEAD4),
    onSecondary = Ink,
    tertiary = Color(0xFFA5B4FC),
    background = Color(0xFF0B1220),
    surface = Color(0xFF111827),
    onBackground = Color(0xFFE2E8F0),
    onSurface = Color(0xFFE2E8F0),
    error = Color(0xFFF87171),
    onError = Ink,
)
