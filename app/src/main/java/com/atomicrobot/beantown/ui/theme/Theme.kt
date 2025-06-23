package com.atomicrobot.beantown.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFe08080),
    secondary = PurpleGrey80,
    tertiary = Pink80,
    surface = Color(0xFFe0e0c0),
    surfaceVariant = Color(0xFF88c058),
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFFe08080),
    secondary = PurpleGrey40,
    tertiary = Pink40,
    surface = Color(0xFFe0e0c0),
    surfaceVariant = Color(0xFF88c058),
)

@Composable
fun BeanTownTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}