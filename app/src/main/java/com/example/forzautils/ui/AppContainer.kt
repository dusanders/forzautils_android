package com.example.forzautils.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.forzautils.ui.theme.ForzaUtilsTheme

@Composable
fun AppContainer(content: @Composable () -> Unit) {
  ForzaUtilsTheme {
    Scaffold(
      Modifier
        .fillMaxSize(),
    ) { innerPadding ->
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(innerPadding)
          .background(MaterialTheme.colorScheme.background)
      ) {
        content()
      }
    }
  }
}