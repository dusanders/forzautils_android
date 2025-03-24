package com.example.forzautils.ui.components.trackMap

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
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
import com.example.forzautils.viewModels.interfaces.IForzaDataStream

@Composable
fun TrackCanvas(
  forzaViewModel: IForzaDataStream,
) {
  val tag = "TrackCanvas"
  val data by forzaViewModel.data.collectAsState()
  var positions = remember { mutableStateOf<List<CanvasCoordinate>>(emptyList()) }
  var canvasCenter by remember { mutableStateOf(CanvasCoordinate(0f, 0f)) }
  var startPosition by remember { mutableStateOf(CanvasCoordinate(0f, 0f)) }
  val currentScalar = remember { mutableFloatStateOf(1f) }
  var path by remember { mutableStateOf(Path()) }
  val primaryColor = MaterialTheme.colorScheme.primary
  var lastPos by remember { mutableStateOf(CanvasCoordinate(0f, 0f)) }

  fun normalizePosition(playerPosition: CanvasCoordinate): CanvasCoordinate {
    val relativeX = (startPosition.x - playerPosition.x)
    val relativeY = (startPosition.y - playerPosition.y)
    return CanvasCoordinate(
      canvasCenter.x + relativeX,
      canvasCenter.y + relativeY
    )
  }

  fun scaleDown(scalar: Float, positionList: List<CanvasCoordinate>): Path {
    val newPath = Path()
    val scaled = positionList.map {
      CanvasCoordinate(
        it.x * scalar,
        it.y * scalar
      )
    }
    scaled.forEachIndexed { index, position ->
      if (index == 0) {
        startPosition = CanvasCoordinate(
          position.x,
          position.y
        )
        newPath.moveTo(canvasCenter.x, canvasCenter.y)
      } else {
        val newPosition = normalizePosition(
          CanvasCoordinate(
            position.x,
            position.y
          )
        )
        if (newPosition.y < 0) {
          return scaleDown(scalar - 0.1f, positionList)
        }
        if (newPosition.x < 0) {
          return scaleDown(scalar - 0.1f, positionList)
        }
        newPath.lineTo(newPosition.x, newPosition.y)
      }
    }
    currentScalar.floatValue = scalar
    lastPos = normalizePosition(
      CanvasCoordinate(
        scaled.last().x,
        scaled.last().y
      )
    )
    positions.value = positionList
    return newPath
  }

  LaunchedEffect(data) {
    if (data == null) {
      return@LaunchedEffect
    }
    val xPos = data!!.positionX
    // NOTE: in telemetry the Z axis represents the forward direction in a 2d top-down view
    val yPos = data!!.positionZ
    val updatedPositions = positions.value
      .plus(CanvasCoordinate(xPos, yPos))
    val calculatedPath = scaleDown(currentScalar.floatValue, updatedPositions)
    path = calculatedPath
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
    drawPath(
      path,
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
      center = Offset(lastPos.x, lastPos.y)
    )
  }
}