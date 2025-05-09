package com.example.forzautils.ui.components.suspension

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.forzautils.ui.components.TextCardBox
import com.example.forzautils.utils.toPrecision
import com.example.forzautils.viewModels.suspension.SuspensionViewModel

@Composable
fun Suspension(
  suspensionViewModel: SuspensionViewModel
) {
  val tag = "Suspension"
  val avgFrontTravelState = suspensionViewModel.averageFrontSuspensionTravel.window.collectAsState()
  val avgRearTravelState = suspensionViewModel.averageRearSuspensionTravel.window.collectAsState()
  val avgDiffState = suspensionViewModel.averageSuspensionDifference.window.collectAsState()
  val frontDiffState = suspensionViewModel.frontDiff.window.collectAsState()
  val rearDiffState = suspensionViewModel.rearDiff.window.collectAsState()

  var frontAvgDisplay by remember { mutableFloatStateOf(0f) }
  var rearAvgDisplay by remember { mutableFloatStateOf(0f) }
  var avgDiffDisplay by remember { mutableFloatStateOf(0f) }
  var frontDiffDisplay by remember { mutableFloatStateOf(0f) }
  var rearDiffDisplay by remember { mutableFloatStateOf(0f) }

  LaunchedEffect(avgFrontTravelState.value) {
    if(avgFrontTravelState.value.isNotEmpty()) {
      frontAvgDisplay = avgFrontTravelState.value.last().toPrecision(2)
    }
  }
  LaunchedEffect(avgRearTravelState.value) {
    if(avgRearTravelState.value.isNotEmpty()) {
      rearAvgDisplay = avgRearTravelState.value.last().toPrecision(2)
    }
  }
  LaunchedEffect(avgDiffState.value) {
    if(avgDiffState.value.isNotEmpty()) {
      avgDiffDisplay = avgDiffState.value.last().toPrecision(2)
    }
  }
  LaunchedEffect(frontDiffState.value) {
    if(frontDiffState.value.isNotEmpty()) {
      frontDiffDisplay = frontDiffState.value.last().toPrecision(2)
    }
  }
  LaunchedEffect(rearDiffState.value) {
    if(rearDiffState.value.isNotEmpty()) {
      rearDiffDisplay = rearDiffState.value.last().toPrecision(2)
    }
  }

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(12.dp)
  ) {
    SuspensionGraph(
      suspensionViewModel.suspensionWindow.windowSize,
      suspensionViewModel.averageFrontSuspensionTravel.window.collectAsState().value.toList(),
      suspensionViewModel.frontDiff.window.collectAsState().value.toList()
    )
    Row(
      modifier = Modifier
        .fillMaxWidth()
    ) {
      Box(
        modifier = Modifier.weight(1f)
      ) {
        TextCardBox(
          height = 75.dp,
          label = "Average Front Suspension Travel",
          value = frontAvgDisplay.toString()
        )
      }
      Box(
        modifier = Modifier.weight(1f)
      ) {
        TextCardBox(
          height = 75.dp,
          label = "Front Difference",
          value = frontDiffDisplay.toString()
        )
      }
    }
  }
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(12.dp)
  ) {
    SuspensionGraph(
      suspensionViewModel.suspensionWindow.windowSize,
      suspensionViewModel.averageRearSuspensionTravel.window.collectAsState().value.toList(),
      suspensionViewModel.rearDiff.window.collectAsState().value.toList()
    )
    Row(
      modifier = Modifier
        .fillMaxWidth()
    ) {
      Box(
        modifier = Modifier.weight(1f)
      ) {
        TextCardBox(
          height = 75.dp,
          label = "Average Rear Suspension Travel",
          value = rearAvgDisplay.toString()
        )
      }
      Box(
        modifier = Modifier.weight(1f)
      ) {
        TextCardBox(
          height = 75.dp,
          label = "Rear Difference",
          value = rearDiffDisplay.toString()
        )
      }
    }
  }
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(12.dp)
  ) {
    SuspensionGraph(
      suspensionViewModel.suspensionWindow.windowSize,
      suspensionViewModel.averageSuspensionDifference.window.collectAsState().value.toList()
    )
    TextCardBox(
      label = "Average Difference Suspension Travel",
      value = avgDiffDisplay.toString()
    )
  }
}