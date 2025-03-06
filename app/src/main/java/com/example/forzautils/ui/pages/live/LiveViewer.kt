package com.example.forzautils.ui.pages.live

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.forzautils.ui.components.engineInfo.EngineInfo
import com.example.forzautils.viewModels.forzaViewModel.ForzaViewModel
import com.example.forzautils.viewModels.hpTqRatings.HpTqRatingsViewModel
import forza.telemetry.data.models.EngineModel

@Composable
fun LiveViewer(
  forzaViewModel: ForzaViewModel,
) {
  val telemetryData by forzaViewModel.data.collectAsState()
  var engineInfo by remember { mutableStateOf<EngineModel?>(null) }

  LaunchedEffect(telemetryData) {
    if (telemetryData != null) {
      engineInfo = EngineModel(telemetryData!!)
    }
  }

//  if (engineInfo != null) {
    EngineInfo(engineInfo)
//  }
}