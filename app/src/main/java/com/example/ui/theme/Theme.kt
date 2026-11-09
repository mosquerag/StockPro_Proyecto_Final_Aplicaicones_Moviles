package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = StockProGreenPrimary,
    onPrimary = Color.White,
    primaryContainer = StockProGreenContainer,
    onPrimaryContainer = StockProGreenOnContainer,
    secondary = StockProBlue,
    onSecondary = Color.White,
    secondaryContainer = StockProBlueContainer,
    onSecondaryContainer = Color(0xFF003355),
    tertiary = StockProOrange,
    onTertiary = Color.White,
    tertiaryContainer = StockProOrangeContainer,
    onTertiaryContainer = Color(0xFF552200),
    error = StockProRedError,
    onError = Color.White,
    errorContainer = StockProRedContainer,
    onErrorContainer = Color(0xFF550000),
    background = StockProBackground,
    onBackground = StockProTextPrimary,
    surface = StockProSurface,
    onSurface = StockProTextPrimary,
    surfaceVariant = StockProSurfaceVariant,
    onSurfaceVariant = StockProTextSecondary,
    outline = StockProOutline
)

private val DarkColorScheme = darkColorScheme(
    primary = StockProGreenLight,
    onPrimary = Color(0xFF003824),
    primaryContainer = StockProGreenDark,
    onPrimaryContainer = StockProGreenContainer,
    secondary = Color(0xFF74C0FC),
    onSecondary = Color(0xFF003355),
    secondaryContainer = Color(0xFF1864AB),
    onSecondaryContainer = Color(0xFFE7F5FF),
    tertiary = Color(0xFFFF922B),
    onTertiary = Color(0xFF552200),
    background = Color(0xFF121715),
    onBackground = Color(0xFFE6EAE8),
    surface = Color(0xFF1A211E),
    onSurface = Color(0xFFE6EAE8),
    surfaceVariant = Color(0xFF242E2A),
    onSurfaceVariant = Color(0xFFB0BEB8),
    outline = Color(0xFF37443E)
)

@Composable
fun StockProTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
