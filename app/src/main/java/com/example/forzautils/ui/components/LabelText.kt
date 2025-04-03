package com.example.forzautils.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.toUpperCase
import com.example.forzautils.ui.theme.FontSizes


@Composable
fun LabelText(text: String) {
  Text(
    text = text.toUpperCase(Locale.current),
    color = MaterialTheme.colorScheme.onPrimary,
    fontSize = FontSizes.sm,
    textAlign = TextAlign.Center,
  )
}