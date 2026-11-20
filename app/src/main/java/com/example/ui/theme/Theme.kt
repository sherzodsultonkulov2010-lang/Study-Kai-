package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val DarkColorScheme = darkColorScheme(
    primary = IgPrimaryDark,
    onPrimary = IgOnPrimaryDark,
    primaryContainer = IgPrimaryContainerDark,
    onPrimaryContainer = IgOnPrimaryContainerDark,
    secondary = IgSecondaryDark,
    onSecondary = IgOnSecondaryDark,
    secondaryContainer = IgSecondaryContainerDark,
    onSecondaryContainer = IgOnSecondaryContainerDark,
    tertiary = IgTertiaryDark,
    onTertiary = IgOnTertiaryDark,
    tertiaryContainer = IgTertiaryContainerDark,
    onTertiaryContainer = IgOnTertiaryContainerDark,
    background = IgBackgroundDark,
    onBackground = IgTextPrimaryDark,
    surface = IgSurfaceDark,
    onSurface = IgTextPrimaryDark,
    surfaceVariant = IgSurfaceVariantDark,
    onSurfaceVariant = IgTextSecondaryDark,
    outline = IgBorderDark,
    error = Color(0xFFFF6B78)
)

private val LightColorScheme = lightColorScheme(
    primary = IgPrimaryLight,
    onPrimary = Color.White,
    primaryContainer = IgPrimaryContainerLight,
    onPrimaryContainer = IgOnPrimaryContainerLight,
    secondary = IgSecondaryLight,
    onSecondary = Color.White,
    secondaryContainer = IgSecondaryContainerLight,
    onSecondaryContainer = IgOnSecondaryContainerLight,
    tertiary = IgTertiaryLight,
    onTertiary = Color.White,
    tertiaryContainer = IgTertiaryContainerLight,
    onTertiaryContainer = IgOnTertiaryContainerLight,
    background = IgBackgroundLight,
    onBackground = IgTextPrimaryLight,
    surface = IgSurfaceLight,
    onSurface = IgTextPrimaryLight,
    surfaceVariant = IgSurfaceVariantLight,
    onSurfaceVariant = IgTextSecondaryLight,
    outline = IgBorderLight,
    error = ErrorRed,
    errorContainer = ErrorContainerLight
)

val AppShapes = Shapes(
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(22.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

@Composable
fun EduSphereTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        shapes = AppShapes,
        content = content
    )
}
