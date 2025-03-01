package com.example.forzautils.ui.components

import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp

@Composable
fun CardBox(content: @Composable () -> Unit) {
  Column(
    modifier = Modifier
      .border(0.3f.dp, MaterialTheme.colorScheme.onPrimary)
      .padding(26.dp)
      .fillMaxWidth()
      .height(50.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.SpaceEvenly
  ) {
    Log.d("CardBox", "CardBox: ${LocalConfiguration.current.screenHeightDp * 0.6f}")
    content()
  }
}