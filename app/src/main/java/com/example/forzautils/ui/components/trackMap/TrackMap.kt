package com.example.forzautils.ui.components.trackMap

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.forzautils.viewModels.interfaces.IForzaDataStream
import com.example.forzautils.viewModels.tireViewModel.TireViewModel

@Composable
fun TrackMap(
  forzaViewModel: IForzaDataStream
) {
  val tag = "TrackMap"
  var forzaData = forzaViewModel.data.collectAsState()
  var trackName by remember { mutableStateOf("") }
  var tireViewModel by remember { mutableStateOf(TireViewModel(forzaViewModel)) }

  LaunchedEffect(forzaData.value) {
    if (forzaData.value != null && trackName.isEmpty()) {
      trackName = forzaViewModel.data.value!!.getTrackInfo().getCircuit() +
          " @ " +
          forzaViewModel.data.value!!.getTrackInfo().getTrack()
    }
  }
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
      TrackCanvas(forzaViewModel)
//      UndersteerCanvas(tireViewModel, forzaViewModel)
    }
  }
}