package com.phiot.phiot_client.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val PhiOTBlue = Color(0xFF33B5E5)
private val PhiOTBlueDark = Color(0xFF30ADD6)
private val PhiOTAccent = Color(0xFFFF4081)

private val LightColorScheme = lightColorScheme(
    primary = PhiOTBlue,
    onPrimary = Color.White,
    primaryContainer = PhiOTBlueDark,
    onPrimaryContainer = Color.White,
    secondary = PhiOTAccent,
    onSecondary = Color.White,
    background = Color(0xFFF5F7FA),
    onBackground = Color(0xFF1A1C1E),
    surface = Color.White,
    onSurface = Color(0xFF1A1C1E),
    surfaceVariant = Color(0xFFE7EEF3),
    onSurfaceVariant = Color(0xFF5C6670),
)

private val DarkColorScheme = darkColorScheme(
    primary = PhiOTBlue,
    onPrimary = Color(0xFF003544),
    primaryContainer = PhiOTBlueDark,
    onPrimaryContainer = Color.White,
    secondary = PhiOTAccent,
    onSecondary = Color.White,
    background = Color(0xFF101418),
    onBackground = Color(0xFFE2E6EA),
    surface = Color(0xFF161B20),
    onSurface = Color(0xFFE2E6EA),
    surfaceVariant = Color(0xFF2A3138),
    onSurfaceVariant = Color(0xFFB8C0C8),
)

@Composable
fun PhiOTTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
    )
}
