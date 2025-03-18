package com.example.forzautils.ui.pages.replay

import android.util.Log
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.forzautils.ui.ForzaAppBarActions
import com.example.forzautils.ui.components.engineInfo.EngineInfo
import com.example.forzautils.viewModels.EngineInfo.EngineInfoViewModel
import com.example.forzautils.viewModels.replayViewModel.ReplayViewModel

@Composable
fun ReplayViewer(
  replayViewModel: ReplayViewModel,
  appBarActions: ForzaAppBarActions
) {
  val tag = "ReplayViewer"
  var totalSegments by remember { mutableIntStateOf(0) }
  val currentSession by replayViewModel.currentSession.collectAsState()
  val currentSegment by replayViewModel.packetReadCount.collectAsState()
  val engineViewModel = EngineInfoViewModel(replayViewModel)
  val scrollState = rememberLazyListState()
  val flingBehavior = rememberSnapFlingBehavior(scrollState)

  LaunchedEffect(currentSession) {
    totalSegments = currentSession?.totalPackets ?: 0
  }

  DisposableEffect(replayViewModel) {
    replayViewModel.startReplay()
    onDispose {
      replayViewModel.stopReplay()
    }
  }

  DisposableEffect(appBarActions) {
    appBarActions.setTitleElement {
      Text(text = currentSession?.sessionInfo?.trackModel?.getTrack() ?: "Replay Viewer")
    }
    onDispose {
      appBarActions.removeTitleElement()
    }
  }

  Scaffold(
    modifier = Modifier
      .fillMaxSize(),
    topBar = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(bottom = 12.dp)
      ) {
        Text(
          modifier = Modifier
            .fillMaxWidth(),
          text = "$currentSegment / ${totalSegments}",
          textAlign = TextAlign.Center
        )
        Slider(
          currentSegment.toFloat(),
          modifier = Modifier
            .fillMaxWidth()
            .padding(start = 12.dp, end = 12.dp),
          onValueChange = {
            Log.d(tag, "Slider value changed: $it")
            replayViewModel.replayAtOffset(it.toInt())
//            sliderPosition = it
          },
          onValueChangeFinished = {
            Log.d(tag, "Slider value changed finished")
          },
          valueRange = 0f..(totalSegments.toFloat())
        )
      }
    }
  ) { innerPadding ->
    LazyColumn(
      modifier = Modifier
        .padding(innerPadding)
        .fillMaxSize(),
      flingBehavior = flingBehavior,
      content = {
        item {
          EngineInfo(engineViewModel)
        }
      }
    )
  }
}