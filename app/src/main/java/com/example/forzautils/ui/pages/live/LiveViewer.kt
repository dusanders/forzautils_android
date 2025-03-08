package com.example.forzautils.ui.pages.live

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.forzautils.ui.components.engineInfo.EngineInfo
import com.example.forzautils.viewModels.EngineInfo.EngineInfoViewModel
import com.example.forzautils.viewModels.forzaViewModel.ForzaViewModel
import forza.telemetry.data.models.EngineModel

@Composable
fun LiveViewer(
  forzaViewModel: ForzaViewModel,
) {
  val tag = "LiveViewer";
  val telemetryData by forzaViewModel.data.collectAsState()
  var engineInfo by remember { mutableStateOf<EngineModel?>(null) }
  var engineViewModel = EngineInfoViewModel(forzaViewModel)

  LaunchedEffect(telemetryData) {
    Log.d(tag, "data: ${telemetryData?.currentEngineRpm}")
    if (telemetryData != null) {
      engineInfo = EngineModel.fromTelemetryData(telemetryData!!)
    }
  }

//  if (engineInfo != null) {
  EngineInfo(engineViewModel)
//  }
}