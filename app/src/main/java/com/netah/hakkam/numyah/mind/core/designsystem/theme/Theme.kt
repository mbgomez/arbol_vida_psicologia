package com.netah.hakkam.numyah.mind.core.designsystem.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = ForestDeep,
    onPrimary = Linen,
    secondary = GoldSand,
    onSecondary = Bark,
    tertiary = Moss,
    background = Linen,
    onBackground = Bark,
    surface = SoftWhite,
    onSurface = Bark,
    surfaceVariant = Clay,
    onSurfaceVariant = Cedar,
    outline = Sage
)

private val DarkColorScheme = darkColorScheme(
    primary = GoldMist,
    onPrimary = NightForest,
    secondary = GoldSand,
    onSecondary = NightForest,
    tertiary = Sage,
    background = NightForest,
    onBackground = MoonSand,
    surface = ForestDeep,
    onSurface = MoonSand,
    surfaceVariant = Moss,
    onSurfaceVariant = GoldMist,
    outline = Clay
)

@Composable
fun ArbolVidaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = ArbolVidaTypography,
        content = content
    )
}
