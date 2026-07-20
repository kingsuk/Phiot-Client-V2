package com.phiot.phiot_client.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val PhiOTBlue = Color(0xFF33B5E5)
private val PhiOTBlueDark = Color(0xFF30ADD6)
private val PhiOTAccent = Color(0xFFFF4081)

private val LightColorScheme = lightColorScheme(
    primary = PhiOTBlue,
    onPrimary = Color.White,
    primaryContainer = PhiOTBlueDark,
    secondary = PhiOTAccent,
    onSecondary = Color.White,
    background = Color(0xFFF5F7FA),
    onBackground = Color(0xFF1A1C1E),
    surface = Color.White,
    onSurface = Color(0xFF1A1C1E),
)

@Composable
fun PhiOTTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        content = content,
    )
}
