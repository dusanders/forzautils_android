package com.example.forzautils.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.graphics.Color

// Define your colors here
val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

// Define your DarkColorScheme
val DarkColorScheme = androidx.compose.material3.darkColorScheme(
    primary = Color(0xffb6c6d6),
    secondary = Color(0xff79a5d1),
    tertiary = Color(0xffbe8df2),
    background = Color(0xff22282c), // Dark background
    surface = Color(0xff393d41), // Dark surface
    onPrimary = Color(0x66b6c6d6),
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White,
)

// Define your LightColorScheme
val LightColorScheme = androidx.compose.material3.lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40,
    background = Color(0xFFFFFBFE), // Light background
    surface = Color(0xFFFFFBFE), // Light surface
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
)
