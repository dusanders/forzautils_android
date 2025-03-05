package com.example.forzautils.ui.pages.sourceChooser

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.forzautils.ui.components.PageHeading
import com.example.forzautils.ui.components.TextCardBox

@Composable
fun SourceChooserPage() {
  Column {
    PageHeading(
      title = "Choose Source",
      desc = "Select a source to view data from. The app currently only supports viewing " +
          "recorded data or live Forza telemetry."
    )
    Row {
      Box(modifier = Modifier.weight(1f)) {
        TextCardBox(
          height = 75.dp,
          value = "Recorded Data",
          label = "Replay or Demo"
        )
      }
      Box(modifier = Modifier.weight(1f)) {
        TextCardBox(
          height = 75.dp,
          value = "Live Data",
          label = "Live Telemetry"
        )
      }
    }
  }
}