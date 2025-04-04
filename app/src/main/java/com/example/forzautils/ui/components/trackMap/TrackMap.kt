package com.example.forzautils.ui.components.trackMap

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.forzautils.viewModels.trackMap.TrackMapViewModel

@Composable
fun TrackMap(
  trackMapViewModel: TrackMapViewModel,
) {
  val tag = "TrackMap"
  val trackName by trackMapViewModel.trackTitle.collectAsState()

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(start = 24.dp, end = 24.dp, bottom = 24.dp)
      .height(150.dp)
      .clip(RoundedCornerShape(12.dp))
      .background(MaterialTheme.colorScheme.surface)
  ) {
    Text(
      modifier = Modifier
        .fillMaxWidth(),
      textAlign = TextAlign.Center,
      text = trackName,
      color = MaterialTheme.colorScheme.primary,
    )
    Box() {
      TrackCanvas(trackMapViewModel)
    }
  }
}