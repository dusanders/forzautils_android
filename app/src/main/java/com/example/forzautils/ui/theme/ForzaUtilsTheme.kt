package com.example.forzautils.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat


@Composable
fun ForzaUtilsTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  content: @Composable () -> Unit
) {
  val colorScheme = when {
    darkTheme -> DarkColorScheme
    else -> LightColorScheme
  }
  val view = LocalView.current
  val window = (view.context as Activity).window
  SideEffect {
    val windowInsetsController = WindowCompat.getInsetsController(window, view)
    windowInsetsController.isAppearanceLightStatusBars = !darkTheme
    WindowCompat.setDecorFitsSystemWindows(window, false)
  }
  Surface(
    modifier = Modifier
      .fillMaxWidth(),
    color = colorScheme.surface
  ) {
    MaterialTheme(
      colorScheme = colorScheme,
      typography = MaterialTheme.typography,
      content = content
    )
  }
}