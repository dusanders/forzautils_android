package com.example.forzautils.ui.pages.live

import android.util.Log
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.example.forzautils.R
import com.example.forzautils.ui.ForzaAppBarActions
import com.example.forzautils.ui.components.engineInfo.EngineInfo
import com.example.forzautils.ui.components.engineInfo.TabContainer
import com.example.forzautils.ui.components.tireTemps.TireTemps
import com.example.forzautils.viewModels.EngineInfo.EngineInfoViewModel
import com.example.forzautils.viewModels.TireViewModel.TireViewModel
import com.example.forzautils.viewModels.forzaViewModel.ForzaViewModel
import forza.telemetry.data.models.EngineModel

@Composable
fun LiveViewer(
  appBarActions: ForzaAppBarActions,
  forzaViewModel: ForzaViewModel,
) {
  val tag = "LiveViewer"
  val recording by forzaViewModel.recording.collectAsState()
  val engineViewModel = EngineInfoViewModel(forzaViewModel)

  DisposableEffect(Unit) {
    appBarActions.setTitleElement {
      Text(stringResource(R.string.live_viewer))
    }
    onDispose {
      appBarActions.removeTitleElement()
    }
  }

  // Comment for now - should be recording switch?
  DisposableEffect(appBarActions) {
    val actionId = appBarActions.injectElement({
      RecordingSwitch(
        isRecording = recording,
        onToggleRecording = {
          if (recording) {
            forzaViewModel.stopRecording()
          } else {
            forzaViewModel.startRecording()
          }
        }
      )
    })
    onDispose {
      Log.d(tag, "LiveViewer disposed")
      appBarActions.removeElement(actionId)
    }
  }
  LazyColumn(
    content = {
      item {
        EngineInfo(engineViewModel)
      }
      item {
        TireTemps(
          TireViewModel(forzaViewModel)
        )
      }
    }
  )
}