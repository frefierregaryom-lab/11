package com.noor.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalLayoutDirection
import android.app.Activity
import androidx.core.view.WindowCompat
import com.noor.app.data.settings.ThemeMode

private val LightScheme = lightColorScheme(
    primary = Emerald700,
    onPrimary = Color.White,
    primaryContainer = Emerald100,
    onPrimaryContainer = Emerald900,
    secondary = Gold700,
    onSecondary = Color.White,
    secondaryContainer = Gold100,
    onSecondaryContainer = Color(0xFF3B2D07),
    tertiary = Gold500,
    onTertiary = Ink,
    background = Cream,
    onBackground = Ink,
    surface = Color(0xFFFFFDF6),
    onSurface = Ink,
    surfaceVariant = Color(0xFFEDE7D3),
    onSurfaceVariant = Color(0xFF4A5650),
    outline = Color(0xFFB9AF8F),
    outlineVariant = Color(0xFFDCD3B8),
    error = Color(0xFFB3261E)
)

private val DarkScheme = darkColorScheme(
    primary = Emerald400,
    onPrimary = Emerald900,
    primaryContainer = Emerald800,
    onPrimaryContainer = Emerald100,
    secondary = Gold400,
    onSecondary = Color(0xFF3B2D07),
    secondaryContainer = Color(0xFF4A3A10),
    onSecondaryContainer = Gold100,
    tertiary = Gold500,
    onTertiary = Ink,
    background = DarkBackground,
    onBackground = Color(0xFFE8EFEA),
    surface = DarkSurface,
    onSurface = Color(0xFFE8EFEA),
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = Color(0xFFB5C4BC),
    outline = Color(0xFF5E7A70),
    outlineVariant = Color(0xFF2A4A41),
    error = Color(0xFFF2B8B5)
)

private val NoorShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(20.dp),
    large = RoundedCornerShape(28.dp),
    extraLarge = RoundedCornerShape(36.dp)
)

/**
 * الثيم العام: ألوان زمردية وذهبية، واتجاه RTL إجباري، وتكبير الخط حسب إعداد المستخدم.
 */
@Composable
fun NoorTheme(
    themeMode: ThemeMode,
    fontScale: Float,
    content: @Composable () -> Unit
) {
    val dark = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                val controller = WindowCompat.getInsetsController(window, view)
                controller.isAppearanceLightStatusBars = !dark
                controller.isAppearanceLightNavigationBars = !dark
            }
        }
    }
    val baseDensity = LocalDensity.current
    CompositionLocalProvider(
        LocalLayoutDirection provides LayoutDirection.Rtl,
        LocalDensity provides Density(baseDensity.density, baseDensity.fontScale * fontScale)
    ) {
        MaterialTheme(
            colorScheme = if (dark) DarkScheme else LightScheme,
            typography = NoorTypography,
            shapes = NoorShapes,
            content = content
        )
    }
}
