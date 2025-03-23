package com.example.forzautils.ui.components.tireTemps

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.forzautils.viewModels.tireViewModel.TireViewModel

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun TireTemps(
  tireViewModel: TireViewModel
) {
  val leftFrontDynamics by tireViewModel.leftFrontDynamicEvent.window.collectAsState()
  val leftRearDynamics by tireViewModel.leftRearDynamicEvent.window.collectAsState()
  val rightFrontDynamics by tireViewModel.rightFrontDynamicEvent.window.collectAsState()
  val rightRearDynamics by tireViewModel.rightRearDynamicEvent.window.collectAsState()

  Row(
    modifier = Modifier.fillMaxWidth()
  ) {
    TireDynamicsGraph(
      eventList = leftFrontDynamics,
      listSize = tireViewModel.leftFrontDynamicEvent.windowSize,
    )
  }
}