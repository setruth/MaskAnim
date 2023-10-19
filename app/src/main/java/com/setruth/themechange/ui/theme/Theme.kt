package com.setruth.themechange.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
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
import com.setruth.themechange.components.MaskAnimActive
import com.setruth.themechange.components.MaskSurface

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

 val LightColorScheme1 = lightColorScheme(
    primary = Color(0xFF5C6BC0),
    secondary = Color(0xFF9575CD),
    tertiary = Color(0xFF29B6F6),
     background = Color(0xFF9FA8DA)
)
 val LightColorScheme2 = lightColorScheme(
     primary = Color(0xFFFFB74D),
     secondary = Color(0xFFFF7043),
     tertiary = Color(0xFF66BB6A),
     background = Color(0xFFFFAB91)
 )
@Composable
fun MaskAnimTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    customTheme:ColorScheme,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> customTheme
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