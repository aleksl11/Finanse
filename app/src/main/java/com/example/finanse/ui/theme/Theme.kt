package com.example.finanse.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF12522B),  // Deep green for primary actions
    secondary = Color(0xFF1B7A46), // Forest green for secondary actions
    tertiary = Color(0xFF0F3E21),  // Dark, almost black-green for accents

    background = Color(0xFF000000), // True black background
    surface = Color(0xFF121212),    // Slightly elevated dark surface
    onPrimary = Color.White,         // High contrast white on primary green
    onSecondary = Color.White,       // White text on secondary green
    onTertiary = Color(0xFFD3E8DA),  // Soft muted greenish-gray for subtle accents
    onBackground = Color(0xFFE0E0E0), // Light grayish-white text on black background
    onSurface = Color(0xFFE0E0E0),    // Matches onBackground for consistent readability
)




private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF12522B),  // Deep green remains the focal color
    secondary = Color(0xFF4F9B66), // Muted, soft green for secondary actions
    tertiary = Color(0xFF1B7A46),  // Slightly lighter forest green for accents

    background = Color(0xFFF5F8F5), // Soft green-tinted white for backgrounds
    surface = Color(0xFFFFFFFF),    // Pure white for cards and dialog surfaces
    onPrimary = Color.White,         // High contrast white for deep green
    onSecondary = Color.Black,       // Dark text for better readability on lighter secondary green
    onTertiary = Color.White,        // High contrast white for tertiary green
    onBackground = Color(0xFF1E2923), // Deep green-gray text for light backgrounds
    onSurface = Color(0xFF1E2923),    // Matches onBackground for consistent readability
)


@Composable
fun FinanseTheme(
    themeMode: ThemeMode,
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}