package com.example.forzautils.ui.components.engineInfo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.forzautils.viewModels.EngineInfo.EngineInfoViewModel
import com.patrykandpatrick.vico.core.common.data.ExtraStore


@Composable
fun EngineInfo(
  engineInfoViewModel: EngineInfoViewModel
) {
  Column {
    EngineParameters(engineInfoViewModel)
    HpTqGraphContainer(engineInfoViewModel)
  }
}