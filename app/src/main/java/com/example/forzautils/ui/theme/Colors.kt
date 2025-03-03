package com.example.forzautils.ui.theme

import android.content.Context
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Typography
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.core.content.ContextCompat
import com.example.forzautils.R

// Define your colors here
val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

// Define your DarkColorScheme
fun DarkColorScheme(context: Context): ColorScheme {
  return androidx.compose.material3.darkColorScheme(
    primary = Color(context.getColor(R.color.darkTheme_primary)),
    secondary = Color(context.getColor(R.color.darkTheme_secondary)),
    tertiary = Color(context.getColor(R.color.darkTheme_tertiary)),
    background = Color(context.getColor(R.color.darkTheme_background)), // Dark background
    surface = Color(context.getColor(R.color.darkTheme_surface)), // Dark surface
    onPrimary = Color(context.getColor(R.color.darkTheme_onPrimary)),
    onSecondary = Color(context.getColor(R.color.darkTheme_onSecondary)),
    onTertiary = Color(context.getColor(R.color.darkTheme_onTertiary)),
    onBackground = Color(context.getColor(R.color.darkTheme_onBackground)),
    onSurface = Color(context.getColor(R.color.darkTheme_onSurface)),
  )
}

// Define your LightColorScheme
fun LightColorScheme(context: Context): ColorScheme {
  return androidx.compose.material3.lightColorScheme(
    primary = Color(context.getColor(R.color.lightTheme_primary)),
    secondary = Color(context.getColor(R.color.lightTheme_secondary)),
    tertiary = Color(context.getColor(R.color.lightTheme_tertiary)),
    background = Color(context.getColor(R.color.lightTheme_background)), // Light background
    surface = Color(context.getColor(R.color.lightTheme_surface)), // Light surface
    onPrimary = Color(context.getColor(R.color.lightTheme_onPrimary)),
    onSecondary = Color(context.getColor(R.color.lightTheme_onSecondary)),
    onTertiary = Color(context.getColor(R.color.lightTheme_onTertiary)),
    onBackground = Color(context.getColor(R.color.lightTheme_onBackground)),
    onSurface = Color(context.getColor(R.color.lightTheme_onSurface)),
  )
}
