package com.example.forzautils.ui.components.tireTemps

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.forzautils.viewModels.TireViewModel.TireViewModel

@Composable
fun TireTemps(
  tireViewModel: TireViewModel
) {
  val leftFrontTemp by remember { derivedStateOf { tireViewModel.leftRearTemp }}
  val rightFrontTemp by remember { derivedStateOf { tireViewModel.rightFrontTemp }}
  val leftRearTemp by remember { derivedStateOf { tireViewModel.leftRearTemp }}
  val rightRearTemp by remember { derivedStateOf { tireViewModel.rightRearTemp }}

  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceEvenly
  ) {
    Text(
      text = "RF Temp: ${if(rightFrontTemp.isEmpty()) "No Data" else rightFrontTemp.last()}"
    )
    Text(
      text = "LF Temp: ${if(leftFrontTemp.isEmpty()) "No Data" else leftFrontTemp.last()}"
    )
  }
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceEvenly
  ) {
    Text(
      text = "RR Temp: ${if(rightRearTemp.isEmpty()) "No Data" else rightRearTemp.last()}"
    )
    Text(
      text = "LR Temp: ${if(leftRearTemp.isEmpty()) "No Data" else leftRearTemp.last()}"
    )
  }
}