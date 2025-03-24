package com.example.forzautils.ui.components.tireTemps

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.forzautils.viewModels.tireViewModel.TireDynamicsEvent
import com.example.forzautils.viewModels.tireViewModel.TireViewModel

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun TireTemps(
  tireViewModel: TireViewModel
) {
  val frontAvg by tireViewModel.frontAvgDynamicEvents.window.collectAsState()
  val rearAvg by tireViewModel.rearAvgDynamicEvents.window.collectAsState()

  Column(
    modifier = Modifier.fillMaxWidth()
  ) {
    TireDynamicsGraph(
      eventList = frontAvg,
      listSize = tireViewModel.frontAvgDynamicEvents.windowSize,
    )
    TireDynamicsGraph(
      eventList = rearAvg,
      listSize = tireViewModel.rearAvgDynamicEvents.windowSize,
    )
  }
}