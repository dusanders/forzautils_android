package com.example.forzautils.ui.pages.tune

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.example.forzautils.ui.components.CardBox
import com.example.forzautils.ui.components.LabelText
import com.example.forzautils.ui.components.ThemeSwitch

@Composable
fun CardSwitch(
  title: String,
  checked: Boolean,
  onCheckedChange: (Boolean) -> Unit
) {
  CardBox(
    height = 100.dp
  ) {
    ThemeSwitch(
      height = 25,
      checked = checked,
      onCheckedChange = onCheckedChange
    )
    LabelText(
      text = title
    )
  }
}