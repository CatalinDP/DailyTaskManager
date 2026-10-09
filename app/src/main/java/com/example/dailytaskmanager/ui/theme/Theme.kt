package com.example.dailytaskmanager.ui.theme

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

private val LightColorScheme = lightColorScheme(
    primary = CoralPrimary,
    onPrimary = White,
    primaryContainer = SoftCoralContainer,
    onPrimaryContainer = CoralPrimary,

    secondary = SageGreen,
    onSecondary = White,
    secondaryContainer = CardBeigeSurface,
    onSecondaryContainer = CharcoalText,

    background = CreamBackground,
    onBackground = CharcoalText,

    surface = CreamBackground,
    onSurface = CharcoalText,
    surfaceVariant = CardBeigeSurface,
    onSurfaceVariant = WarmGrayText,

    outline = OutlineBorder,
    outlineVariant = OutlineBorder.copy(alpha = 0.5f),

    error = ErrorRed,
    onError = White,
    errorContainer = ErrorContainer,
    onErrorContainer = Color(0xFF410002)
)

private val DarkColorScheme = darkColorScheme(
    primary = CoralPrimary,
    onPrimary = CharcoalText,
    primaryContainer = DarkCoral,
    onPrimaryContainer = SoftCoralContainer,

    secondary = SageGreen,
    onSecondary = White,
    secondaryContainer = CharcoalContainer,
    onSecondaryContainer = CreamBackground,

    background = CharcoalSurface,
    onBackground = CreamBackground,

    surface = CharcoalSurface,
    onSurface = CreamBackground,
    surfaceVariant = CharcoalContainer,
    onSurfaceVariant = OutlineBorder,

    outline = WarmGrayText,
    outlineVariant = CharcoalContainer
)

@Composable
fun DailyTaskManagerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Se eliminó la rama dinámica para usar siempre tus colores
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}