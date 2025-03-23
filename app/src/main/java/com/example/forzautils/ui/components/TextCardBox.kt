package com.example.forzautils.ui.components

import android.annotation.SuppressLint
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.forzautils.ui.theme.FontSizes

@Composable
fun ValueText(text: String, color: Color? = null) {
  Text(
    text = text,
    color = color ?: MaterialTheme.colorScheme.primary,
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
    textAlign = TextAlign.Center
  )
}

@Composable
fun TextCardBox(
  onClicked: (() -> Unit)? = null,
  onLongClicked: (() -> Unit)? = null,
  height: Dp = 50.dp,
  padding: Dp = 12.dp,
  label: String? = null,
  value: String,
  valueColor: Color? = MaterialTheme.colorScheme.primary,
) {
  CardBox(
    height = height,
    padding = padding,
    onClicked = onClicked,
    onLongClicked = onLongClicked
  ) {
    ValueText(
      text = value,
      color = valueColor
    )
    if (label != null)
      LabelText(text = label)
  }
}