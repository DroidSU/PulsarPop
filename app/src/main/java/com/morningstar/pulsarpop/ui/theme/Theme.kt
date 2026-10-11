package com.morningstar.pulsarpop.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = CosmicPrimaryDark,
    onPrimary = CosmicOnPrimaryDark,
    primaryContainer = CosmicPrimaryContainerDark,
    onPrimaryContainer = CosmicOnPrimaryContainerDark,
    secondary = CosmicSecondaryDark,
    onSecondary = CosmicOnSecondaryDark,
    secondaryContainer = CosmicSecondaryContainerDark,
    onSecondaryContainer = CosmicOnSecondaryContainerDark,
    tertiary = CosmicTertiaryDark,
    onTertiary = CosmicOnTertiaryDark,
    tertiaryContainer = CosmicTertiaryContainerDark,
    onTertiaryContainer = CosmicOnTertiaryContainerDark,
    background = CosmicBackgroundDark,
    onBackground = CosmicOnBackgroundDark,
    surface = CosmicSurfaceDark,
    onSurface = CosmicOnSurfaceDark,
    surfaceVariant = CosmicSurfaceVariantDark,
    onSurfaceVariant = CosmicOnSurfaceVariantDark
)

private val LightColorScheme = lightColorScheme(
    primary = CosmicPrimaryLight,
    onPrimary = CosmicOnPrimaryLight,
    primaryContainer = CosmicPrimaryContainerLight,
    onPrimaryContainer = CosmicOnPrimaryContainerLight,
    secondary = CosmicSecondaryLight,
    onSecondary = CosmicOnSecondaryLight,
    secondaryContainer = CosmicSecondaryContainerLight,
    onSecondaryContainer = CosmicOnSecondaryContainerLight,
    tertiary = CosmicTertiaryLight,
    onTertiary = CosmicOnTertiaryLight,
    tertiaryContainer = CosmicTertiaryContainerLight,
    onTertiaryContainer = CosmicOnTertiaryContainerLight,
    background = CosmicBackgroundLight,
    onBackground = CosmicOnBackgroundLight,
    surface = CosmicSurfaceLight,
    onSurface = CosmicOnSurfaceLight,
    surfaceVariant = CosmicSurfaceVariantLight,
    onSurfaceVariant = CosmicOnSurfaceVariantLight
)

@Composable
fun getTileColors(darkTheme: Boolean = isSystemInDarkTheme()): List<Color> {
    return if (darkTheme) {
        listOf(
            TileNeonPinkDark,
            TileCyanBlueDark,
            TileEmeraldGreenDark,
            TileSolarAmberDark,
            TileDeepVioletDark
        )
    } else {
        listOf(
            TileNeonPinkLight,
            TileCyanBlueLight,
            TileEmeraldGreenLight,
            TileSolarAmberLight,
            TileDeepVioletLight
        )
    }
}

@Composable
fun PulsarPopTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
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
