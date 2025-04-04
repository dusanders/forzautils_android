package com.example.forzautils.ui.components

import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ThemeSwitch(
  height: Int = 50,
  checked: Boolean,
  onCheckedChange: (Boolean) -> Unit
) {
  Switch(
    modifier = Modifier.height(height.dp),
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