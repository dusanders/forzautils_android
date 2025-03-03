package com.example.forzautils.ui.components

import android.annotation.SuppressLint
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.forzautils.ui.theme.FontSizes

@Composable
fun ValueText(text: String) {
  Text(
    text = text,
    color = MaterialTheme.colorScheme.primary,
    fontSize = FontSizes.md,
    fontWeight = FontWeight.Bold
  )
}

@Composable
fun LabelText(text: String) {
  Text(
    text = text.toUpperCase(Locale.current),
    color = MaterialTheme.colorScheme.onPrimary,
    fontSize = FontSizes.sm,
  )
}

@Composable
fun TextCardBox(
  height: Dp = 50.dp,
  label: String,
  value: String
) {
  CardBox(
    height = height
  ) {
    ValueText(text = value)
    LabelText(text = label)
  }
}