package com.example.csci215_final.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// ── Palette ──────────────────────────────────────────────────────────────────
private val Indigo700 = Color(0xFF3949AB)
private val Indigo500 = Color(0xFF5C6BC0)
private val Indigo200 = Color(0xFF9FA8DA)
private val Teal400 = Color(0xFF26C6DA)
private val Teal200 = Color(0xFF80DEEA)
private val Amber500 = Color(0xFFFFC107)
private val Surface = Color(0xFFF5F5FF)
private val Background = Color(0xFFEEEEF8)
private val DarkSurface = Color(0xFF1A1B2E)
private val DarkBg = Color(0xFF12131F)

private val LightColors = lightColorScheme(
    primary = Indigo700,
    onPrimary = Color.White,
    primaryContainer = Indigo200,
    onPrimaryContainer = Color(0xFF0D1256),
    secondary = Teal400,
    onSecondary = Color.Black,
    secondaryContainer = Teal200,
    onSecondaryContainer = Color(0xFF00363D),
    tertiary = Amber500,
    onTertiary = Color.Black,
    background = Background,
    onBackground = Color(0xFF1A1B2E),
    surface = Surface,
    onSurface = Color(0xFF1A1B2E),
    surfaceVariant = Color(0xFFE3E4F0),
    onSurfaceVariant = Color(0xFF44475A),
    error = Color(0xFFB00020),
    onError = Color.White,
)

private val DarkColors = darkColorScheme(
    primary = Indigo200,
    onPrimary = Color(0xFF0D1256),
    primaryContainer = Indigo500,
    onPrimaryContainer = Color.White,
    secondary = Teal200,
    onSecondary = Color(0xFF00363D),
    secondaryContainer = Color(0xFF004F58),
    onSecondaryContainer = Teal200,
    tertiary = Amber500,
    onTertiary = Color.Black,
    background = DarkBg,
    onBackground = Color(0xFFE3E4F6),
    surface = DarkSurface,
    onSurface = Color(0xFFE3E4F6),
    surfaceVariant = Color(0xFF2E2F45),
    onSurfaceVariant = Color(0xFFC5C6DC),
    error = Color(0xFFCF6679),
    onError = Color.Black,
)

@Composable
fun TodoAppTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content,
    )
}
