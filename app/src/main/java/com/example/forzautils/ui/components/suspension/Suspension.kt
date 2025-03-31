package com.example.forzautils.ui.components.suspension

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.forzautils.ui.components.TextCardBox
import com.example.forzautils.utils.toPrecision
import com.example.forzautils.viewModels.suspension.SuspensionViewModel

@Composable
fun Suspension(
  suspensionViewModel: SuspensionViewModel
) {
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
          value = suspensionViewModel.averageFrontSuspensionTravel.window
            .collectAsState().value.last().toPrecision(2).toString()
        )
      }
      Box(
        modifier = Modifier.weight(1f)
      ) {
        TextCardBox(
          height = 75.dp,
          label = "Front Difference",
          value = suspensionViewModel.frontDiff.window
            .collectAsState().value.last().toPrecision(2).toString()
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
          value = suspensionViewModel.averageRearSuspensionTravel.window
            .collectAsState().value.last().toPrecision(2).toString()
        )
      }
      Box(
        modifier = Modifier.weight(1f)
      ) {
        TextCardBox(
          height = 75.dp,
          label = "Rear Difference",
          value = suspensionViewModel.rearDiff.window
            .collectAsState().value.last().toPrecision(2).toString()
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
      value = suspensionViewModel.averageSuspensionDifference.window
        .collectAsState().value.last().toPrecision(2).toString()
    )
  }
}