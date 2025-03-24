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
import com.example.forzautils.viewModels.forzaViewModel.ForzaViewModel
import com.example.forzautils.viewModels.interfaces.IForzaDataStream
import com.example.forzautils.viewModels.tireViewModel.TireViewModel

data class UndersteerSegment(
  var path: Path,
  var isFinished: Boolean
)

@Composable
fun UndersteerCanvas(
  tireViewModel: TireViewModel,
  forzaViewModel: IForzaDataStream
) {
  val tag = "UndersteerCanvas"
  var canvasCenter by remember { mutableStateOf(CanvasCoordinate(0f, 0f)) }
  var startPosition by remember { mutableStateOf(CanvasCoordinate(0f, 0f)) }
  val frontAvg by tireViewModel.frontAvgDynamicEvents.window.collectAsState()
  val forzaData by forzaViewModel.data.collectAsState()
  val segments = remember { mutableStateOf(emptyList<UndersteerSegment>()) }

  fun normalizePosition(playerPosition: CanvasCoordinate): CanvasCoordinate {
    val relativeX = (startPosition.x - playerPosition.x)
    val relativeY = (startPosition.y - playerPosition.y)
    return CanvasCoordinate(
      canvasCenter.x + relativeX,
      canvasCenter.y + relativeY
    )
  }

  LaunchedEffect(forzaData) {
    if (frontAvg.isEmpty()) {
      return@LaunchedEffect
    }
    if (forzaData == null) {
      return@LaunchedEffect
    }
    if (startPosition.y == 0f && startPosition.x == 0f) {
      startPosition = CanvasCoordinate(
        forzaData!!.positionX,
        forzaData!!.positionZ
      )
    }
    if (frontAvg.last().ratio > 0.5) {
      val playerPosition = CanvasCoordinate(
        forzaData!!.positionX,
        forzaData!!.positionZ
      )
      val normalizedPosition = normalizePosition(playerPosition)
      if (segments.value.isEmpty()) {
        val newSegment = UndersteerSegment(Path(), false)
        newSegment.path.moveTo(normalizedPosition.x, normalizedPosition.y)
        segments.value = segments.value.plus(newSegment)
      }
      if (segments.value.last().isFinished) {
        val newSegment = UndersteerSegment(Path(), false)
        newSegment.path.moveTo(normalizedPosition.x, normalizedPosition.y)
        segments.value = segments.value.plus(newSegment)
      } else {
        val lastSegment = segments.value.last()
        lastSegment.path.lineTo(normalizedPosition.x, normalizedPosition.y)
        segments.value = segments.value
          .minus(lastSegment)
          .plus(lastSegment)
      }
    } else if (segments.value.isNotEmpty()) {
      segments.value.last().isFinished = true
    }
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