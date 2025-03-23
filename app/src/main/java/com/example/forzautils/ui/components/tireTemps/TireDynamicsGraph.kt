package com.example.forzautils.ui.components.tireTemps

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.unit.dp
import com.example.forzautils.viewModels.tireViewModel.TireDynamicsEvent

data class Coordinate(
  val x: Float,
  val y: Float
)

@Composable
fun TireDynamicsGraph(
  listSize: Int = 100,
  eventList: List<TireDynamicsEvent>
) {
  val tag = "TireDynamicsGraph"
  val tempScalar = 1
  val slipScalar = 1
  val ratioScalar = 1
  var xNormalizer by remember { mutableFloatStateOf(0f) }
  var layoutHeight by remember { mutableFloatStateOf(0f) }
  var layoutWidth by remember { mutableFloatStateOf(0f) }
  var centerCoordinate by remember { mutableStateOf(Coordinate(0f, 0f)) }

  LaunchedEffect(layoutWidth, listSize) {
    val xMovements = (layoutWidth / listSize)
    Log.d(tag, "xMovements: $layoutWidth / ${listSize} = $xMovements")
    xNormalizer = xMovements
  }

  val steerColor = Color.Red
  val tempColor = Color.Green
  val slipColor = Color.Cyan
  val ratioColor = Color.Magenta
  val steerYBaseline = centerCoordinate.y
  val tempYBaseline = centerCoordinate.y
  val slipYBaseline = centerCoordinate.y
  val ratioYBaseline = centerCoordinate.y
  val steerPath = Path()
  val tempPath = Path()
  val slipPath = Path()
  val ratioPath = Path()
  eventList.forEachIndexed { index, event ->
    if (index == 0) {
      tempPath.moveTo(0f, tempYBaseline)
      steerPath.moveTo(0f, steerYBaseline)
      slipPath.moveTo(0f, slipYBaseline)
      ratioPath.moveTo(0f, ratioYBaseline)
    } else {
      val xMove = index.toFloat() * xNormalizer
      steerPath.lineTo(
        xMove,
        steerYBaseline - event.steeringAngle
      )
      tempPath.lineTo(
        xMove,
        tempYBaseline - (event.temp * tempScalar)
      )
      slipPath.lineTo(
        xMove,
        slipYBaseline - (event.slip * slipScalar)
      )
      ratioPath.lineTo(
        xMove,
        ratioYBaseline - (event.combinedSlip * ratioScalar)
      )
    }
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(24.dp)
      .clip(RoundedCornerShape(12.dp))
  ) {
    Text(
      modifier = Modifier.padding(bottom = 12.dp),
      text = "Steer angle: ${eventList.lastOrNull()?.steeringAngle ?: 0f}",
      color = steerColor,
    )
    Text(
      modifier = Modifier.padding(bottom = 12.dp),
      text = "Temp: ${(eventList.lastOrNull()?.temp ?: 0f) * tempScalar}",
      color = tempColor,
    )
    Text (
      modifier = Modifier.padding(bottom = 12.dp),
      text = "Slip: ${(eventList.lastOrNull()?.slip ?: 0f) * slipScalar}",
      color = slipColor,
    )
    Text (
      text = "Ratio: ${(eventList.lastOrNull()?.ratio ?: 0f) * ratioScalar}",
      color = ratioColor,
      modifier = Modifier.padding(bottom = 12.dp)
    )
    Canvas(
      modifier = Modifier
        .fillMaxWidth()
        .height(250.dp)
        .background(MaterialTheme.colorScheme.surface)
        .onPlaced { coords ->
          layoutHeight = coords.size.height.toFloat()
          layoutWidth = coords.size.width.toFloat()
          centerCoordinate = Coordinate(
            x = layoutWidth / 2,
            y = layoutHeight / 2
          )
        }
    ) {
      drawPath(
        steerPath,
        color = steerColor,
        style = Stroke(
          width = 1.5.dp.toPx(),
          pathEffect = PathEffect.cornerPathEffect(8.dp.toPx()),
          cap = Stroke.DefaultCap
        )
      )
      drawPath(
        tempPath,
        color = tempColor,
        style = Stroke(
          width = 1.5.dp.toPx(),
          pathEffect = PathEffect.cornerPathEffect(8.dp.toPx()),
          cap = Stroke.DefaultCap
        )
      )
      drawPath(
        slipPath,
        color = slipColor,
        style = Stroke(
          width = 1.5.dp.toPx(),
          pathEffect = PathEffect.cornerPathEffect(8.dp.toPx()),
          cap = Stroke.DefaultCap
        )
      )
      drawPath(
        ratioPath,
        color = ratioColor,
        style = Stroke(
          width = 1.5.dp.toPx(),
          pathEffect = PathEffect.cornerPathEffect(8.dp.toPx()),
          cap = Stroke.DefaultCap
        )
      )
    }
  }
}