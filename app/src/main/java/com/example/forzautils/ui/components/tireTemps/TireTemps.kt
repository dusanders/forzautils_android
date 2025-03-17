package com.example.forzautils.ui.components.tireTemps

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.forzautils.viewModels.TireViewModel.TireViewModel

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun TireTemps(
  tireViewModel: TireViewModel
) {
  val leftFrontTemp = tireViewModel.leftFrontTemp.collectAsState()
  val rightFrontTemp = tireViewModel.rightFrontTemp.collectAsState()
  val leftRearTemp = tireViewModel.leftRearTemp.collectAsState()
  val rightRearTemp = tireViewModel.rightRearTemp.collectAsState()

  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceEvenly
  ) {
    Box(
      modifier = Modifier
        .weight(1f)
        .height(120.dp)
    ) {
      TireTempGraph(
        label = "Left Front",
        dataPoints = leftFrontTemp.value
      )
    }
    Box(
      modifier = Modifier
        .weight(1f)
        .height(120.dp)
    ) {
      TireTempGraph(
        label = "Right Front",
        dataPoints = rightFrontTemp.value
      )
    }
  }
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceEvenly
  ) {
//    Text(
//      text = "RR Temp: ${if (rightRearTemp.value.window.isEmpty()) "No Data" else rightRearTemp.last()}"
//    )
//    Text(
//      text = "LR Temp: ${if (leftRearTemp.isEmpty()) "No Data" else leftRearTemp.last()}"
//    )
  }
}