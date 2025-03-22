package com.example.forzautils.ui.components.engineInfo

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.forzautils.R
import com.example.forzautils.ui.components.TextCardBox
import com.example.forzautils.ui.theme.FontSizes
import com.example.forzautils.viewModels.EngineInfo.EngineInfoViewModel

@Composable
fun HpTqGraphContainer(
  engineInfoViewModel: EngineInfoViewModel
) {
  val tag = "HpTqGraphContainer"
  val hpTqRatings by engineInfoViewModel.powerMap.collectAsState()
  var selectedGear by remember { mutableIntStateOf(-1) }
  var selectedColor = MaterialTheme.colorScheme.surface

  val thisContext = LocalContext.current
  LaunchedEffect(hpTqRatings) {
    if (hpTqRatings.isEmpty()) {
      return@LaunchedEffect
    } else if (selectedGear == -1) {
      selectedGear = hpTqRatings.keys.first()
    }
  }

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(bottom = 12.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(start = 12.dp, end = 12.dp, top = 12.dp)
        .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
        .background(MaterialTheme.colorScheme.surface)
    ) {
      PowerGraph(
        hpTqRatings[selectedGear]?.values?.map {
          PowerAtRpm(
            it.getRoundedRpm(),
            it.getHorsepower(),
            it.torque
          )
        } ?: emptyList()
      )
    }
    Row(
      modifier = Modifier
        .padding(bottom = 12.dp, start = 12.dp, end = 12.dp)
    ) {
      if (hpTqRatings.isNotEmpty()) {
        hpTqRatings.keys.sorted().map {
          Box(
            modifier = Modifier
              .weight(1f)
              .background(if(it == selectedGear) selectedColor else Color.Transparent)
          ) {
            TextCardBox(
              padding = 4.dp,
              value = thisContext.getString(R.string.gear_formatted, it),
              onClicked = {
                selectedGear = it
              }
            )
          }
        }
      } else {
        TextCardBox(
          value = stringResource(R.string.tabContainer_noData)
        )
      }
    }
  }
}