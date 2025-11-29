@file:Suppress("unused")
package com.example.absolutecinema.ui.theme

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
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.systemuicontroller.rememberSystemUiController

// Use the requested card/background color everywhere: #0e1216
private val AppDarkBackground = Color(0xFF000000)

// Keep existing tokens for primary/secondary (or replace with your preferred colors)
private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80,
    background = AppDarkBackground,
    surface = AppDarkBackground,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40
    // if you ever enable light scheme, you can set different tokens here
)

/**
 * AbsoluteCinemaTheme
 *
 * - Forces dark mode app-wide by default so app UI always uses dark palette.
 * - Sets system bars (status + navigation) color to the app dark background (#0e1216)
 *   and forces white icons (darkIcons = false).
 *
 * Note: add accompanist system UI controller dependency if not already present:
 * implementation("com.google.accompanist:accompanist-systemuicontroller:<version>")
 */
@Composable
fun AbsoluteCinemaTheme(
    // force dark by default so the app always looks like dark mode
    darkTheme: Boolean = true,
    // dynamic color kept as an option but we ensure background/surface match AppDarkBackground
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            // derive dynamic dark scheme but override background/surface/on* to our dark tokens
            dynamicDarkColorScheme(context).copy(
                background = AppDarkBackground,
                surface = AppDarkBackground,
                onBackground = Color.White,
                onSurface = Color.White
            )
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    // set system bars color to match app background and request light (white) icons
    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController.setSystemBarsColor(
            color = AppDarkBackground,
            darkIcons = false // false => use light icons (white)
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}