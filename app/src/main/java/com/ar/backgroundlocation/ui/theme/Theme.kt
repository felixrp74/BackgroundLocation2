package com.ar.backgroundlocation.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// 🌊 Paleta oceánica
private val OceanBlue = Color(0xFF1D6AA8)         // Azul profundo
private val Turquoise = Color(0xFF00ACC1)         // Verde agua
private val SeaFoam = Color(0xFFE0F7FA)           // Espuma marina
private val CoralAccent = Color(0xFFFF7043)       // Coral suave
private val DeepNavy = Color(0xFF002F6C)          // Azul noche
private val WhiteText = Color(0xFFFFFFFF)

private val OceanLightColors = lightColorScheme(
    primary = OceanBlue,
    onPrimary = WhiteText,
    secondary = Turquoise,
    onSecondary = Color.White,
    background = SeaFoam,
    onBackground = DeepNavy,
    surface = Color.White,
    onSurface = DeepNavy,
    error = CoralAccent,
    onError = Color.White
)

private val OceanDarkColors = darkColorScheme(
    primary = DeepNavy,
    onPrimary = WhiteText,
    secondary = Turquoise,
    onSecondary = Color.Black,
    background = Color.Black,
    onBackground = SeaFoam,
    surface = DeepNavy,
    onSurface = SeaFoam,
    error = CoralAccent,
    onError = Color.Black
)

@Composable
fun BackGroundLocationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Desactivamos colores dinámicos para usar nuestra paleta
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) OceanDarkColors else OceanLightColors

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}