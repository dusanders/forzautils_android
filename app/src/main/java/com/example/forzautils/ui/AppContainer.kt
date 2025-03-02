package com.example.forzautils.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import com.example.forzautils.ui.theme.ForzaUtilsTheme
import com.example.forzautils.viewModels.themeViewModel.ThemeViewModel

@Composable
fun AppContainer(
  content: @Composable () -> AppBarActionHandlers) {
  var actionHandler by rememberSaveable { mutableStateOf<AppBarActionHandlers?>(null) }
  Scaffold(
    Modifier
      .fillMaxSize(),
    topBar = {
      ForzaAppBar(actionHandler)
    }
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .background(MaterialTheme.colorScheme.background)
    ) {
      actionHandler = content()
    }
  }
}