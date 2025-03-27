package com.example.forzautils.ui.pages.replay

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.forzautils.viewModels.replay.ReplayViewModel

@Composable
fun ReplayList(
  replayViewModel: ReplayViewModel,
  navigateToReplayViewer: () -> Unit
) {
  val allSessions by replayViewModel.allSessions.collectAsState()
  var promptDeleteSession by remember { mutableStateOf("") }

  LaunchedEffect(replayViewModel) {
    replayViewModel.updateAllSessions()
  }

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
        SessionItemLayout(
          session = it,
          replayViewModel = replayViewModel,
          onLongClicked = {
            promptDeleteSession = it.id
          },
          onClicked = {
            replayViewModel.openReplaySession(it)
            navigateToReplayViewer()
          }
        )
      }
    }
  }
  if(promptDeleteSession.isNotEmpty()) {
    DeleteDialog(
      onDismiss = {
        promptDeleteSession = ""
      },
      onDelete = {
        replayViewModel.deleteSession(promptDeleteSession)
        promptDeleteSession = ""
      }
    )
  }
}