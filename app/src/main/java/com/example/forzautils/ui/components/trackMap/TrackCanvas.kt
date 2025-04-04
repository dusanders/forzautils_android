package com.example.forzautils.ui.components.trackMap

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.unit.dp
import com.example.forzautils.utils.CanvasCoordinate
import com.example.forzautils.viewModels.trackMap.TrackMapViewModel

@Composable
fun TrackCanvas(
  trackMapViewModel: TrackMapViewModel
) {
  val tag = "TrackCanvas"
  var drawPath by remember { mutableStateOf(Path()) }
  val path = trackMapViewModel.trackPath.collectAsState()
  val primaryColor = MaterialTheme.colorScheme.primary
  val currentPosition = trackMapViewModel.currentPosition.collectAsState()

  LaunchedEffect(path.value) {
    drawPath = path.value
  }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .padding(20.dp)
  ) {
    Canvas(
      modifier = Modifier
        .fillMaxSize()
        .onPlaced { coords ->
          trackMapViewModel.setCanvasLayout(
            coords.size.width.toFloat(),
            coords.size.height.toFloat()
          )
        },
    ) {
      drawPath(
        drawPath,
        color = primaryColor,
        style = Stroke(
          width = 3.dp.toPx(),
          pathEffect = PathEffect.cornerPathEffect(8.dp.toPx()),
          cap = Stroke.DefaultCap
        )
      )
      if (currentPosition.value != null) {
        drawCircle(
          color = primaryColor,
          radius = 5.dp.toPx(),
          center = Offset(
            currentPosition.value!!.x,
            currentPosition.value!!.y
          )
        )
      }
    }
//    UndersteerCanvas(
//      trackMapViewModel,
//      trackMapViewModel,
//    )
  }
}