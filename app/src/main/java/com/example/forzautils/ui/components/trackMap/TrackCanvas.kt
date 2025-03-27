package com.example.forzautils.ui.components.trackMap

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
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
  var canvasCenter by remember { mutableStateOf<CanvasCoordinate?>(null) }
  val currentScalar = remember { mutableFloatStateOf(1f) }
  val path = trackMapViewModel.trackPath.collectAsState()
  val primaryColor = MaterialTheme.colorScheme.primary

  Box(
    modifier = Modifier
      .fillMaxSize()
  ) {
    Canvas(
      modifier = Modifier
        .fillMaxSize()
        .onPlaced { coords ->
          val height = coords.size.height
          val width = coords.size.width
          canvasCenter = CanvasCoordinate(
            x = (width / 2).toFloat(),
            y = (height / 2).toFloat()
          )
          trackMapViewModel.redrawWithCenterAndScalar(
            canvasCenter!!,
            currentScalar.floatValue
          )
        },
    ) {
      drawPath(
        path.value,
        color = primaryColor,
        style = Stroke(
          width = 3.dp.toPx(),
          pathEffect = PathEffect.cornerPathEffect(8.dp.toPx()),
          cap = Stroke.DefaultCap
        )
      )
      drawCircle(
        color = primaryColor,
        radius = 5.dp.toPx(),
        center = Offset(
          trackMapViewModel.currentPosition.value.x,
          trackMapViewModel.currentPosition.value.y
        )
      )
    }
    UndersteerCanvas(
      trackMapViewModel,
      trackMapViewModel.absoluteStartPosition,
    )
  }
}