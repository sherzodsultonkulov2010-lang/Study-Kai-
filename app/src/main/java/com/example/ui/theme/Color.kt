package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Instagram Signature Gradient Stops
val IgAmberGold = Color(0xFFFCAF45)
val IgSunsetOrange = Color(0xFFF77737)
val IgCrimsonRed = Color(0xFFFD1D1D)
val IgRosePink = Color(0xFFE1306C)
val IgMagenta = Color(0xFFC13584)
val IgRoyalPurple = Color(0xFF833AB4)

val InstagramGradientColors = listOf(
    IgAmberGold,
    IgSunsetOrange,
    IgRosePink,
    IgMagenta,
    IgRoyalPurple
)

val InstagramBrush: Brush = Brush.linearGradient(InstagramGradientColors)

// Light Scheme Tokens (Clean Instagram White + Rose/Purple/Amber)
val IgPrimaryLight = IgRosePink
val IgPrimaryContainerLight = Color(0xFFFDE8F0)
val IgOnPrimaryContainerLight = Color(0xFF7A0D38)

val IgSecondaryLight = IgRoyalPurple
val IgSecondaryContainerLight = Color(0xFFF3E5FA)
val IgOnSecondaryContainerLight = Color(0xFF421066)

val IgTertiaryLight = IgSunsetOrange
val IgTertiaryContainerLight = Color(0xFFFFF0DB)
val IgOnTertiaryContainerLight = Color(0xFF6E2B06)

val IgBackgroundLight = Color(0xFFFAFAFA)
val IgSurfaceLight = Color(0xFFFFFFFF)
val IgSurfaceVariantLight = Color(0xFFEFEFEF)
val IgTextPrimaryLight = Color(0xFF262626)
val IgTextSecondaryLight = Color(0xFF737373)
val IgBorderLight = Color(0xFFDBDBDB)

// Dark Scheme Tokens (True Instagram Dark)
val IgPrimaryDark = Color(0xFFFF5C8D)
val IgOnPrimaryDark = Color(0xFF3B0018)
val IgPrimaryContainerDark = Color(0xFF691134)
val IgOnPrimaryContainerDark = Color(0xFFFFD9E3)

val IgSecondaryDark = Color(0xFFC084FC)
val IgOnSecondaryDark = Color(0xFF2D0A4E)
val IgSecondaryContainerDark = Color(0xFF4C1D72)
val IgOnSecondaryContainerDark = Color(0xFFF3E8FF)

val IgTertiaryDark = IgAmberGold
val IgOnTertiaryDark = Color(0xFF451A03)
val IgTertiaryContainerDark = Color(0xFF6B3407)
val IgOnTertiaryContainerDark = Color(0xFFFFE6C2)

val IgBackgroundDark = Color(0xFF000000)
val IgSurfaceDark = Color(0xFF121212)
val IgSurfaceVariantDark = Color(0xFF262626)
val IgTextPrimaryDark = Color(0xFFF5F5F5)
val IgTextSecondaryDark = Color(0xFFA8A8A8)
val IgBorderDark = Color(0xFF363636)

val ErrorRed = Color(0xFFED4956)
val ErrorContainerLight = Color(0xFFFEE2E2)
val SuccessGreen = Color(0xFF16A34A)
