package com.example.forzautils.ui.pages.sourceChooser

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.forzautils.R
import com.example.forzautils.ui.components.PageHeading
import com.example.forzautils.ui.components.TextCardBox

@Composable
fun SourceChooserPage(
  navigateToLiveViewer: () -> Unit,
  navigateToReplayViewer: () -> Unit
) {
  Column {
    PageHeading(
      title = stringResource(R.string.sourcePage_heading),
      desc = stringResource(R.string.sourcePage_desc)
    )
    Row {
      Box(modifier = Modifier.weight(1f)) {
        TextCardBox(
          height = 75.dp,
          value = stringResource(R.string.sourcePage_replayBtn),
          label = stringResource(R.string.sourcePage_replayDesc),
        )
      }
      Box(modifier = Modifier.weight(1f)) {
        TextCardBox(
          height = 75.dp,
          value = stringResource(R.string.sourcePage_liveBtn),
          label = stringResource(R.string.sourcePage_replayDesc),
          onClicked = navigateToLiveViewer
        )
      }
    }
  }
}