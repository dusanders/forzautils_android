package com.example.forzautils.ui.components

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun TextInput(
  hint: String? = null,
  value: String,
  onValueChange: (String) -> Unit,
  onDonePressed: () -> Unit = {},
  desc: String? = null,
  isNumeric: Boolean = true
) {
  CardBox(
    height = 100.dp,
  ) {
    TextField(
      value = value,
      onValueChange = { onValueChange(it) },
      label = { Text(hint ?: "") },
      colors = TextFieldDefaults.colors(
        focusedTextColor = MaterialTheme.colorScheme.primary,
        unfocusedTextColor = MaterialTheme.colorScheme.primary,
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        disabledIndicatorColor = Color.Transparent
      ),
      singleLine = true,
      keyboardActions = KeyboardActions(
        onDone = {onDonePressed()}
      ),
      keyboardOptions = if (isNumeric)
        KeyboardOptions(
          keyboardType = KeyboardType.Number,
          imeAction = ImeAction.Done
        )
      else
        KeyboardOptions(
          keyboardType = KeyboardType.Text,
          imeAction = ImeAction.Done
        )
    )
    if (desc != null) {
      LabelText(text = desc)
    }
  }
}