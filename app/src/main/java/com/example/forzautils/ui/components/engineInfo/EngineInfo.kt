package com.example.forzautils.ui.components.engineInfo

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import com.example.forzautils.viewModels.engineInfo.EngineInfoViewModel


@Composable
fun EngineInfo(
  engineInfoViewModel: EngineInfoViewModel
) {
  Column {
    EngineParameters(engineInfoViewModel)
    HpTqGraphContainer(engineInfoViewModel)
  }
}