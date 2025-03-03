package com.example.forzautils.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun CardBox(
  height: Dp = 50.dp,
  content: @Composable () -> Unit
) {
  Column(
    modifier = Modifier
      .border(Dp.Hairline, MaterialTheme.colorScheme.onPrimary)
      .padding(26.dp)
      .fillMaxWidth()
      .height(height),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.SpaceAround
  ) {
    content()
  }
}