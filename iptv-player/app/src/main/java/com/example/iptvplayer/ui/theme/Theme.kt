package com.example.iptvplayer.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Accent = Color(0xFF5B8CFF)
private val AccentDark = Color(0xFF3F6FE0)

private val DarkColors = darkColorScheme(
    primary = Accent,
    onPrimary = Color.White,
    secondary = AccentDark,
    background = Color(0xFF0C0F14),
    surface = Color(0xFF151A22),
    surfaceVariant = Color(0xFF1E2530),
    onBackground = Color(0xFFE6EAF2),
    onSurface = Color(0xFFE6EAF2),
)

private val LightColors = lightColorScheme(
    primary = AccentDark,
    secondary = Accent,
)

@Composable
fun IptvTheme(
    // TV / media apps default to dark; honor system setting on phones.
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = Typography(),
        content = content,
    )
}
