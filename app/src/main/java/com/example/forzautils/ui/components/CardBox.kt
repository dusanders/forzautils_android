package com.example.forzautils.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
  onClicked: (() -> Unit)? = null,
  height: Dp = 50.dp,
  content: @Composable () -> Unit
) {
  var modifier = Modifier
    .border(Dp.Hairline, MaterialTheme.colorScheme.onPrimary)
  if (onClicked != null) {
    modifier = modifier
      .clickable(
        onClick = onClicked
      )
  }
  modifier = modifier
    .padding(26.dp)
    .fillMaxWidth()
    .height(height)
  Column(
    modifier = modifier,
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.SpaceAround
  ) {
    content()
  }
}