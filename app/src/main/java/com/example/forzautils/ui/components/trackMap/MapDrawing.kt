package com.example.forzautils.ui.components.trackMap

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Path
import com.example.forzautils.viewModels.interfaces.IForzaDataStream

data class PlayerPosition(
  val positionX: Float,
  val positionY: Float
)

@Composable
fun MapDrawing(
  forzaViewModel: IForzaDataStream,
  centerX: Float,
  centerY: Float,
  content: @Composable (path: Path, current: PlayerPosition) -> Unit
) {
  val tag = "MapDrawing"
  val data by forzaViewModel.data.collectAsState()
  var positions = remember { mutableStateOf<List<PlayerPosition>>(emptyList()) }
  var startPosition by remember { mutableStateOf(PlayerPosition(0f, 0f)) }
  val currentScalar = remember { mutableFloatStateOf(1f) }
  var path = remember { mutableStateOf(Path()) }
  var lastPos by remember { mutableStateOf(PlayerPosition(0f, 0f)) }

  fun normalizePosition(playerPosition: PlayerPosition): PlayerPosition {
    val relativeX = (startPosition.positionX - playerPosition.positionX)
    val relativeY = (startPosition.positionY - playerPosition.positionY)
    return PlayerPosition(
      centerX + relativeX,
      centerY + relativeY
    )
  }

  fun scaleDown(scalar: Float, positionList: List<PlayerPosition>): Path {
    val newPath = Path()
    val scaled = positionList.map {
      PlayerPosition(
        it.positionX * scalar,
        it.positionY * scalar
      )
    }
    scaled.forEachIndexed { index, position ->
      if (index == 0) {
        startPosition = PlayerPosition(
          position.positionX,
          position.positionY
        )
        newPath.moveTo(centerX, centerY)
      } else {
        val newPosition = normalizePosition(
          PlayerPosition(
            position.positionX,
            position.positionY
          )
        )
        if (newPosition.positionY < 0) {
          return scaleDown(scalar - 0.1f, positionList)
        }
        if (newPosition.positionX < 0) {
          return scaleDown(scalar - 0.1f, positionList)
        }
        newPath.lineTo(newPosition.positionX, newPosition.positionY)
      }
    }
    currentScalar.floatValue = scalar
    lastPos = normalizePosition(
      PlayerPosition(
        scaled.last().positionX,
        scaled.last().positionY
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
    val updatedPositions = positions.value.plus(PlayerPosition(xPos, yPos))
    val calculatedPath = scaleDown(currentScalar.floatValue, updatedPositions)
    path.value = calculatedPath
  }

  content(path.value, lastPos)
}