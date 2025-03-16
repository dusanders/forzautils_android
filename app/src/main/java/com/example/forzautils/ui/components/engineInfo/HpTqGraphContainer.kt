package com.example.forzautils.ui.components.engineInfo

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.example.forzautils.R
import com.example.forzautils.viewModels.EngineInfo.EngineInfoViewModel

@Composable
fun HpTqGraphContainer(
  engineInfoViewModel: EngineInfoViewModel
){
  val hpTqRatings by engineInfoViewModel.powerMap.collectAsState()
  var hpGraphs by remember {
    mutableStateOf<Map<String, @Composable () -> Unit>>(emptyMap())
  }

  val thisContext = LocalContext.current
  LaunchedEffect(hpTqRatings) {
    val newGraphs = HashMap<String, @Composable () -> Unit>()
    for(it in hpTqRatings.keys) {
      val keyString = thisContext.getString(R.string.gear_formatted, it)
      newGraphs[keyString] = {
        HpTqGraph(it, hpTqRatings[it]!!)
      }
    }
    hpGraphs = newGraphs.toSortedMap()
  }

  TabContainer(
    "Hp / Tq Ratings",
    hpGraphs
  )
}