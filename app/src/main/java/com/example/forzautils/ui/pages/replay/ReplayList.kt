package com.example.forzautils.ui.pages.replay

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.forzautils.ui.components.CardBox
import com.example.forzautils.ui.components.TextCardBox
import com.example.forzautils.viewModels.replayViewModel.ReplayViewModel

@Composable
fun ReplayList(
  replayViewModel: ReplayViewModel,
  navigateToReplayViewer: () -> Unit
) {
  val allSessions by replayViewModel.allSessions.collectAsState()

  if (allSessions.isEmpty()) {
    Column(
      modifier = Modifier.fillMaxSize(),
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(
        text = "No Sessions Found!"
      )
    }
  } else {
    LazyColumn(
      modifier = Modifier.fillMaxSize(),
    ) {
      items(allSessions) {
        TextCardBox(
          label = it.date,
          value = replayViewModel.getSessionInfo(it)?.trackModel?.getTrack() ?: "Unknown",
          onClicked = {
            replayViewModel.openReplaySession(it)
            navigateToReplayViewer()
//            if(replayViewModel.openReplaySession(it.id)){
//              navigateToReplayViewer()
//            } else {
//              // TODO - failed to load the replay session!
//            }
          }
        )
      }
    }
  }
}