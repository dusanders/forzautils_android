package com.example.forzautils.ui.components.appBarFlyout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.forzautils.ui.components.ThemeSwitch
import com.example.forzautils.viewModels.themeViewModel.ThemeViewModel

@Composable
fun LightModeSwitch(
  themeViewModel: ThemeViewModel
) {
  val isDarkTheme by themeViewModel.isDarkTheme.collectAsState()
  Row(
    modifier = Modifier.fillMaxWidth(),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Icon(
      imageVector = Icons.Rounded.LightMode,
      contentDescription = "Theme Switch",
    )
    ThemeSwitch(
      checked = !isDarkTheme,
      onCheckedChange = {
        themeViewModel.setTheme(!isDarkTheme)
      }
    )
  }
}