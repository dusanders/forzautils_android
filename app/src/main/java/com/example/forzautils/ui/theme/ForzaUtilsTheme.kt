package com.example.forzautils.ui.theme

import android.app.Activity
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.example.forzautils.viewModels.themeViewModel.ThemeViewModel


@Composable
fun ForzaUtilsTheme(
  viewModel: ThemeViewModel,
  content: @Composable () -> Unit
) {
  val isDarkTheme = viewModel.isDarkTheme.collectAsState()
  val currentTheme = if (isDarkTheme.value) DarkColorScheme else LightColorScheme

  val view = LocalView.current
  val window = (LocalView.current.context as Activity).window
  SideEffect {
    val windowInsetsController = WindowCompat.getInsetsController(window, view)
    windowInsetsController.isAppearanceLightStatusBars = !viewModel.isDarkTheme.value
    WindowCompat.setDecorFitsSystemWindows(window, false)
  }

  Surface(
    modifier = Modifier
      .fillMaxWidth(),
    color = currentTheme.surface
  ) {
    MaterialTheme(
      colorScheme = currentTheme,
      typography = MaterialTheme.typography,
      content = content
    )
  }
}