package com.example.forzautils.ui.components.appBarFlyout

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.forzautils.viewModels.themeViewModel.ThemeViewModel

@Composable
fun ThemeSwitch(
  themeViewModel: ThemeViewModel
) {
  val isDarkTheme by themeViewModel.isDarkTheme.collectAsState()
  Row(
    verticalAlignment = Alignment.CenterVertically
  ) {
    Icon(
      imageVector = Icons.Rounded.LightMode,
      contentDescription = "Settings",
      modifier = Modifier.padding(end = 30.dp)
    )
    Switch(
      checked = !isDarkTheme,
      onCheckedChange = {
        themeViewModel.setTheme(!isDarkTheme)
      }
    )
  }
}