package com.example.dailytaskmanager.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = Indigo,
    onPrimary = Color.White,
    primaryContainer = IndigoContainer,
    onPrimaryContainer = IndigoDeepText,

    secondary = SlateSecondary,
    onSecondary = Color.White,
    secondaryContainer = SlateContainer,
    onSecondaryContainer = SlateDeepText,

    tertiary = Butter,
    onTertiary = Color.White,
    tertiaryContainer = ButterContainer,
    onTertiaryContainer = ButterDeepText,

    background = IvoryBackground,
    onBackground = InkText,

    surface = IvoryBackground,
    onSurface = InkText,
    surfaceVariant = SurfaceContainerLight,
    onSurfaceVariant = MutedText,
    surfaceContainerLowest = SurfaceLowestLight,
    surfaceContainerLow = SurfaceLowLight,
    surfaceContainer = SurfaceContainerLight,
    surfaceContainerHigh = SurfaceHighLight,
    surfaceContainerHighest = SurfaceHighestLight,

    inverseSurface = Color(0xFF303036),
    inverseOnSurface = Color(0xFFF2F0F7),
    inversePrimary = IndigoLight,

    outline = OutlineLight,
    outlineVariant = OutlineVariantLight,

    error = ErrorRed,
    onError = Color.White,
    errorContainer = ErrorContainer,
    onErrorContainer = Color(0xFF410002)
)

private val DarkColorScheme = darkColorScheme(
    primary = IndigoLight,
    onPrimary = OnIndigoLight,
    primaryContainer = IndigoDarkContainer,
    onPrimaryContainer = IndigoContainer,

    secondary = SlateLight,
    onSecondary = SlateDeepText,
    secondaryContainer = SlateDarkContainer,
    onSecondaryContainer = SlateContainer,

    tertiary = ButterLight,
    onTertiary = ButterDeepText,
    tertiaryContainer = ButterDarkContainer,
    onTertiaryContainer = ButterContainer,

    background = NightBackground,
    onBackground = NightText,

    surface = NightBackground,
    onSurface = NightText,
    surfaceVariant = SurfaceHighDark,
    onSurfaceVariant = NightMutedText,
    surfaceContainerLowest = SurfaceLowestDark,
    surfaceContainerLow = SurfaceLowDark,
    surfaceContainer = SurfaceContainerDark,
    surfaceContainerHigh = SurfaceHighDark,
    surfaceContainerHighest = SurfaceHighestDark,

    inverseSurface = NightText,
    inverseOnSurface = Color(0xFF303036),
    inversePrimary = Indigo,

    outline = OutlineDark,
    outlineVariant = OutlineVariantDark,

    error = ErrorLight,
    onError = Color(0xFF690005),
    errorContainer = ErrorDarkContainer,
    onErrorContainer = ErrorContainer
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