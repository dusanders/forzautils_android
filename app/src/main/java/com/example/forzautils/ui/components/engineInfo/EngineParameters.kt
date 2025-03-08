package com.example.forzautils.ui.components.engineInfo

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.forzautils.viewModels.EngineInfo.EngineInfoViewModel

@Composable
fun EngineParameters(
  viewModel: EngineInfoViewModel
) {
  val tag = "EngineParameters"
  val currentRpm by viewModel.rpm.collectAsState()
  val currentThrottle by viewModel.throttle.collectAsState()
  val currentGear by viewModel.gear.collectAsState()

  Row() {
    Text(
      text = "RPM: $currentRpm"
    )
    Text(
      text = "Throttle: $currentThrottle"
    )
    Text (
      text = "Gear: $currentGear"
    )
  }
}