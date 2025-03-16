package com.example.forzautils.ui.pages.live

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.forzautils.ui.components.ThemeSwitch

@Composable
fun RecordingSwitch(
  isRecording: Boolean,
  onToggleRecording: () -> Unit
) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Text(
      text = "Recording",
    )
    ThemeSwitch (
      checked = isRecording,
      onCheckedChange = {
        onToggleRecording()
      }
    )
  }
}
