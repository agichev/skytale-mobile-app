package group.skytale.app.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import group.skytale.app.data.AppSettings
import group.skytale.app.data.ThemeMode

private val darkScheme = darkColorScheme(
    primary = Color(0xFF9C2A2A),
    onPrimary = Color(0xFFFDF4F2),
    secondary = Color(0xFFB89778),
    tertiary = Color(0xFF8E96C9),
    background = Color(0xFF090809),
    surface = Color(0xFF121012),
    surfaceContainer = Color(0xFF181416),
    surfaceVariant = Color(0xFF261B1D),
    onBackground = Color(0xFFF5F0E8),
    onSurface = Color(0xFFF5F0E8),
    onSurfaceVariant = Color(0xFFD9C6BE),
    outline = Color(0xFF5E4646),
)

private val lightScheme = lightColorScheme(
    primary = Color(0xFF8B1E1E),
    onPrimary = Color.White,
    secondary = Color(0xFF8E6A4D),
    tertiary = Color(0xFF31427F),
    background = Color(0xFFF4EEE9),
    surface = Color(0xFFFFFBF7),
    surfaceContainer = Color(0xFFF0E5DE),
    surfaceVariant = Color(0xFFE2D3CC),
    onBackground = Color(0xFF1A1213),
    onSurface = Color(0xFF1A1213),
    onSurfaceVariant = Color(0xFF4C3739),
    outline = Color(0xFF7F6363),
)

private val monoDarkScheme = darkColorScheme(
    primary = Color(0xFFF2F2F2),
    onPrimary = Color(0xFF090909),
    secondary = Color(0xFFBEBEBE),
    tertiary = Color(0xFFE2E2E2),
    background = Color(0xFF000000),
    surface = Color(0xFF0B0B0B),
    surfaceContainer = Color(0xFF121212),
    surfaceVariant = Color(0xFF1B1B1B),
    onBackground = Color(0xFFFFFFFF),
    onSurface = Color(0xFFFFFFFF),
    onSurfaceVariant = Color(0xFFBBBBBB),
    outline = Color(0xFF4A4A4A),
)

private val lightClassicScheme = lightColorScheme(
    primary = Color(0xFFA52B2B),
    onPrimary = Color(0xFFFFFFFF),
    secondary = Color(0xFFB89778),
    tertiary = Color(0xFF5C6AB0),
    background = Color(0xFFF8F2ED),
    surface = Color(0xFFFFFFFF),
    surfaceContainer = Color(0xFFF1E7E0),
    surfaceVariant = Color(0xFFE7DBD4),
    onBackground = Color(0xFF221617),
    onSurface = Color(0xFF221617),
    onSurfaceVariant = Color(0xFF6B5252),
    outline = Color(0xFF9A7A7A),
)

private val telegramDarkScheme = darkColorScheme(
    primary = Color(0xFF64A9E9),
    onPrimary = Color(0xFF0E1A25),
    secondary = Color(0xFF4D89C7),
    tertiary = Color(0xFF87C7FF),
    background = Color(0xFF0E1621),
    surface = Color(0xFF17212B),
    surfaceContainer = Color(0xFF1E2A36),
    surfaceVariant = Color(0xFF233242),
    onBackground = Color(0xFFE6EEF7),
    onSurface = Color(0xFFE6EEF7),
    onSurfaceVariant = Color(0xFF8EA8C1),
    outline = Color(0xFF36506A),
)

private val Typography = androidx.compose.material3.Typography(
    displaySmall = TextStyle(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Bold,
        fontSize = 36.sp,
        lineHeight = 42.sp,
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp,
        lineHeight = 34.sp,
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 26.sp,
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 18.sp,
    ),
)

@Composable
fun SkytaleTheme(
    settings: AppSettings,
    content: @Composable () -> Unit,
) {
    val useDark = when (settings.themeMode) {
        ThemeMode.DARK -> true
        ThemeMode.LIGHT -> false
        ThemeMode.MONO_DARK -> true
        ThemeMode.LIGHT_CLASSIC -> false
        ThemeMode.TELEGRAM_DARK -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }
    val colorScheme = when (settings.themeMode) {
        ThemeMode.MONO_DARK -> monoDarkScheme
        ThemeMode.LIGHT_CLASSIC -> lightScheme
        ThemeMode.TELEGRAM_DARK -> telegramDarkScheme
        else -> if (useDark) darkScheme else lightScheme
    }
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
    )
}
