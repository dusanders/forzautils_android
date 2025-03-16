package com.example.forzautils.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
fun ThemeSwitch(
  checked: Boolean,
  onCheckedChange: (Boolean) -> Unit
) {
  Switch(
    checked = checked,
    onCheckedChange = {
      onCheckedChange(it)
    },
    colors = SwitchDefaults.colors(
      checkedThumbColor = MaterialTheme.colorScheme.tertiary,
      checkedTrackColor = MaterialTheme.colorScheme.secondary,
      uncheckedThumbColor = MaterialTheme.colorScheme.secondary,
      uncheckedTrackColor = MaterialTheme.colorScheme.onSecondary,
    )
  )
}