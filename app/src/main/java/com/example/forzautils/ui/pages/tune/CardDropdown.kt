package com.example.forzautils.ui.pages.tune

import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.forzautils.ui.components.CardBox
import com.example.forzautils.ui.components.LabelText
import com.example.forzautils.ui.components.ValueText

@Composable
fun CardDropdown(
  label: String,
  options: List<String>,
  selectedOption: String,
  onOptionSelected: (String) -> Unit
) {
  var expanded by remember { mutableStateOf(false) }
  CardBox(
    onClicked = { expanded = true }
  ) {
    ValueText(
      text = selectedOption
    )
    DropdownMenu(
      expanded = expanded,
      onDismissRequest = { expanded = false },
    ) {
      options.forEach { option ->
        DropdownMenuItem(
          text = { Text(text = option) },
          onClick = {
            onOptionSelected(option)
            expanded = false
          }
        )
      }
    }
    LabelText(
      text = label
    )
  }
}