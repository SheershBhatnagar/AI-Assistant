package dev.sheershbhatnagar.ai_assistant.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    background = DarkMoon,
    primary = PearlWhite,
    secondary = Palladium,
    tertiary = Aria,
    surface = Onyx,
    onPrimary = DarkMoon,
    onSecondary = DarkMoon,
    onBackground = PearlWhite,
    onSurface = PearlWhite,
)

private val LightColorScheme = lightColorScheme(
    background = PearlWhite,
    primary = DarkMoon,
    secondary = Aria,
    surface = ChefHat,
    onPrimary = White,
    onSecondary = Palladium,
    onBackground = OilRush,
)

@Composable
fun AIAssistantTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme =
        when {
            dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                val context = LocalContext.current
                if (darkTheme) dynamicDarkColorScheme(
                    context
                ) else dynamicLightColorScheme(
                    context
                )
            }

            darkTheme -> DarkColorScheme
            else -> LightColorScheme
        }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
