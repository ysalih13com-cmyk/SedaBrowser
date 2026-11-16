package com.seda.browser.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColors = lightColorScheme(
    primary = SedaPink,
    onPrimary = SedaWhite,
    primaryContainer = SedaPinkLight,
    onPrimaryContainer = SedaPinkDark,
    secondary = SedaGold,
    onSecondary = SedaDark,
    background = SedaSurfaceLight,
    surface = SedaSurfaceLight,
    onBackground = SedaDark,
    onSurface = SedaDark
)

private val DarkColors = darkColorScheme(
    primary = SedaPinkLight,
    onPrimary = SedaDark,
    primaryContainer = SedaPinkDark,
    onPrimaryContainer = SedaPinkLight,
    secondary = SedaGold,
    onSecondary = SedaDark,
    background = SedaSurfaceDark,
    surface = SedaSurfaceDark,
    onBackground = SedaWhite,
    onSurface = SedaWhite
)

@Composable
fun SedaBrowserTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors
    val view = LocalView.current

    if (!view.isInEditMode) {
        val window = (LocalContext.current as? Activity)?.window
        if (window != null) {
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = SedaTypography,
        content = content
    )
}
