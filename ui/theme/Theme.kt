package com.mass.notiguard.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

/** 扁平化、轻量、干净的配色（以靛蓝为主色，浅灰背景） */
val Primary = Color(0xFF3D5AFE)
val PrimaryContainer = Color(0xFFE8ECFF)
val Background = Color(0xFFF5F6FA)
val SurfaceColor = Color(0xFFFFFFFF)
val OnSurface = Color(0xFF1B1B1F)
val Muted = Color(0xFF8A8F99)

val LightColors = lightColorScheme(
    primary = Primary,
    primaryContainer = PrimaryContainer,
    background = Background,
    surface = SurfaceColor,
    onBackground = OnSurface,
    onSurface = OnSurface,
    onSurfaceVariant = Muted,
    outline = Color(0xFFE3E5EC)
)

val DarkColors = darkColorScheme(
    primary = Primary,
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E)
)

@Composable
fun NotiGuardTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content
    )
}
