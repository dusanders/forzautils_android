package com.example.forzautils.ui.pages.replay

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.example.forzautils.dataModels.RecordedSession
import com.example.forzautils.ui.components.TextCardBox
import com.example.forzautils.viewModels.replay.ReplayViewModel

@Composable
fun SessionItemLayout(
  session: RecordedSession,
  replayViewModel: ReplayViewModel,
  onLongClicked: () -> Unit,
  onClicked: () -> Unit,
) {

  fun generateSessionTitle(session: RecordedSession): String {
    val sessionInfo = replayViewModel.getSessionInfo(session)
    var track = "Unknown"
    var circuit = "Unknown"
    if(sessionInfo != null) {
      track = sessionInfo.trackModel.getTrack()
      circuit = sessionInfo.trackModel.getCircuit()
    }
    return "$circuit - $track"
  }

  fun generateSessionSubtitle(session: RecordedSession): String {
    val sessionInfo = replayViewModel.getSessionInfo(session)
    var car = "Unknown"
    if(sessionInfo != null) {
      car = sessionInfo.carModel.getName()
    }
    return "$car \n ${session.date}"
  }

  TextCardBox(
    height = 100.dp,
    padding = 24.dp,
    label = generateSessionSubtitle(session),
    value = generateSessionTitle(session),
    onLongClicked = {
      onLongClicked()
    },
    onClicked = {
      onClicked()
    }
  )
}