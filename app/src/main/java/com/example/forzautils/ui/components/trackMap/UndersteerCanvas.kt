package com.example.forzautils.ui.components.trackMap

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.unit.dp
import com.example.forzautils.utils.CanvasCoordinate
import com.example.forzautils.viewModels.trackMap.TrackMapViewModel

data class UndersteerSegment(
  var path: Path,
  var isFinished: Boolean
)

@Composable
fun UndersteerCanvas(
  trackMapViewModel: TrackMapViewModel,
  startPositionActual: CanvasCoordinate?,
) {
  val tag = "UndersteerCanvas"
  var canvasCenter by remember { mutableStateOf(CanvasCoordinate(0f, 0f)) }
  val events by trackMapViewModel.understeerWindow.window.collectAsState()
  val segments = remember { mutableStateOf(emptyList<UndersteerSegment>()) }
  val scalar by trackMapViewModel.currentScalar.collectAsState()

  fun normalizePosition(playerPosition: CanvasCoordinate): CanvasCoordinate {
    val relativeX = ((startPositionActual!!.x * scalar) - (playerPosition.x * scalar))
    val relativeY = ((startPositionActual!!.y * scalar) - (playerPosition.y * scalar))
    return CanvasCoordinate(
      canvasCenter.x + relativeX,
      canvasCenter.y + relativeY
    )
  }

  LaunchedEffect(canvasCenter, events, scalar) {
    if(startPositionActual == null) {
      return@LaunchedEffect
    }
    var updatedSegments = emptyList<UndersteerSegment>()
    events.forEach {
      val newSegment = UndersteerSegment(Path(), false)
      it.coordinates.forEachIndexed { index, coordinate ->
        val normalizedCoordinate = normalizePosition(coordinate)
        if (index == 0) {
          newSegment.path.moveTo(normalizedCoordinate.x, normalizedCoordinate.y)
        } else {
          newSegment.path.lineTo(normalizedCoordinate.x, normalizedCoordinate.y)
        }
      }
      newSegment.isFinished = true
      updatedSegments = updatedSegments.plus(newSegment)
    }
    segments.value = updatedSegments
  }

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
      },
  ) {
    segments.value.forEach { segment ->
      drawPath(
        segment.path,
        color = Color.Red,
        style = Stroke(
          width = 3.dp.toPx(),
          pathEffect = PathEffect.cornerPathEffect(8.dp.toPx()),
          cap = Stroke.DefaultCap
        )
      )
    }
  }
}