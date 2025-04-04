package com.example.forzautils.ui.components.engineInfo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.example.forzautils.R
import com.example.forzautils.ui.components.RadialGauge
import com.example.forzautils.ui.theme.FontSizes
import com.example.forzautils.viewModels.engineInfo.EngineInfoViewModel

@Composable
fun EngineParameters(
  viewModel: EngineInfoViewModel
) {
  val tag = "EngineParameters"
  val currentRpm by viewModel.rpm.collectAsState()
  val currentThrottle by viewModel.throttle.collectAsState()
  val currentGear by viewModel.gear.collectAsState()
  val minRpm by viewModel.minRpm.collectAsState()
  val maxRpm by viewModel.maxRpm.collectAsState()

  Row(
    modifier = Modifier
      .fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceEvenly
  ) {
    RadialGauge(
      stringResource(R.string.generic_rpmLabel),
      currentRpm.toFloat(),
      minRpm.toFloat(),
      maxRpm.toFloat()
    )
    Column(
      modifier = Modifier
        .align(Alignment.CenterVertically),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.SpaceEvenly
    ) {
      Text(
        textAlign = TextAlign.Center,
        text = stringResource(R.string.generic_gearLabel),
        fontSize = FontSizes.header
      )
      Text(
        textAlign = TextAlign.Center,
        text = currentGear.toString(),
        fontSize = FontSizes.banner,
        fontWeight = FontWeight(900)
      )
    }
    RadialGauge(
      stringResource(R.string.generic_throttleLabel),
      currentThrottle.toFloat()
    )
  }
}