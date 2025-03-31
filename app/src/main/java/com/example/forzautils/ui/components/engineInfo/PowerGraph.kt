package com.example.forzautils.ui.components.engineInfo

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import com.example.forzautils.R
import com.example.forzautils.ui.theme.FontSizes
import com.example.forzautils.utils.CanvasCoordinate
import kotlin.math.roundToInt

data class PowerAtRpm(
  val rpm: Int,
  val horsepower: Float,
  val torque: Float,
)

@Composable
fun PowerGraph(
  data: List<PowerAtRpm>
) {
  val tag = "PowerGraph"
  val sorted = data.sortedBy { it.rpm }
  val textMeasurer = rememberTextMeasurer()
  val textColor = MaterialTheme.colorScheme.primary
  val hpColor = colorResource(R.color.hpTorque_hpLine)
  val tqColor = colorResource(R.color.hpTorque_torqueLine)
  var xNormalizer by remember { mutableFloatStateOf(0f) }
  var yNormalizer by remember { mutableFloatStateOf(0f) }
  var layoutHeight by remember { mutableFloatStateOf(0f) }
  var layoutWidth by remember { mutableFloatStateOf(0f) }
  var centerCoordinate by remember { mutableStateOf(CanvasCoordinate(0f, 0f)) }
  var hpPath by remember { mutableStateOf(Path()) }
  var tqPath by remember { mutableStateOf(Path()) }
  var bottomLabelMeasure by remember { mutableFloatStateOf(0f) }
  var yLabelMeasure by remember { mutableFloatStateOf(0f) }
  var xLabelMeasure by remember { mutableFloatStateOf(0f) }
  var bottomLabelY by remember { mutableFloatStateOf(0f) }
  var xLabelY by remember { mutableFloatStateOf(0f) }
  var yLabelYBase by remember { mutableFloatStateOf(0f) }

  fun measureText(value: String): TextLayoutResult {
    return textMeasurer.measure(
      text = value,
      style = TextStyle(
        color = textColor,
        fontSize = FontSizes.lg
      )
    )
  }

  fun findMinMax(data: List<PowerAtRpm>): Pair<Float, Float> {
    var result = Pair(0f, 0f)
    if (data.isEmpty()) {
      return result;
    }
    val min = data.minOf { it.horsepower }
      .coerceAtMost(data.minOf { it.torque })
    val max = data.maxOf { it.horsepower }
      .coerceAtLeast(data.maxOf { it.torque })
    return Pair(min, max)
  }

  fun findYPosition(value: Float, minMax: Pair<Float, Float>): Float {
    val normalized = (value - minMax.first) / (minMax.second - minMax.first)
    val scaled = normalized * yLabelYBase
    return yLabelYBase - scaled
  }

  fun roundRpmToStep(rpm: Int): Int {
    return (rpm * 0.01f).roundToInt() * 100
  }

  fun calculatePaths() {
    val newHpPath = Path()
    val newTqPath = Path()
    val minMax = findMinMax(sorted)
    sorted.forEachIndexed { index, powerAtRpm ->
      if (index == 0) {
        newHpPath.moveTo(
          yLabelMeasure,
          findYPosition(powerAtRpm.horsepower, minMax)
        )
        newTqPath.moveTo(
          yLabelMeasure,
          findYPosition(powerAtRpm.torque, minMax)
        )
      } else {
        newHpPath.lineTo(
          yLabelMeasure + (xNormalizer * index),
          findYPosition(powerAtRpm.horsepower, minMax)
        )
        newTqPath.lineTo(
          yLabelMeasure + (xNormalizer * index),
          findYPosition(powerAtRpm.torque, minMax)
        )
      }
    }

    hpPath = newHpPath
    tqPath = newTqPath
  }

  LaunchedEffect(layoutWidth, sorted.size) {
    if (sorted.isEmpty()) {
      return@LaunchedEffect
    }
    val xMovements = (layoutWidth / (sorted.size - 1))
    val minMax = findMinMax(sorted)
    val yMovements = (layoutHeight / (minMax.second - minMax.first))
    xNormalizer = xMovements
    yNormalizer = yMovements
    bottomLabelMeasure = measureText("RPM").size.height.toFloat()
    yLabelMeasure = measureText(minMax.second.toInt().toString()).size.width.toFloat()
    xLabelMeasure = measureText(sorted.last().rpm.toString()).size.height.toFloat()
    bottomLabelY = layoutHeight - bottomLabelMeasure
    xLabelY = bottomLabelY - xLabelMeasure
    yLabelYBase = xLabelY - (xLabelMeasure / 2)
    calculatePaths()
  }

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(24.dp)
  ) {
    Canvas(
      modifier = Modifier
        .fillMaxWidth()
        .height(175.dp)
        .onPlaced { layout ->
          layoutHeight = layout.size.height.toFloat()
          layoutWidth = layout.size.width.toFloat()
          centerCoordinate = CanvasCoordinate(
            layoutWidth / 2,
            layoutHeight / 2
          )
        },
    ) {
      fun drawPowerPaths() {
        val powerPathStyle = Stroke(
          width = 1.5.dp.toPx(),
          pathEffect = PathEffect.cornerPathEffect(8.dp.toPx()),
        )
        drawPath(
          path = hpPath,
          color = hpColor,
          style = powerPathStyle
        )
        drawPath(
          path = tqPath,
          color = tqColor,
          style = powerPathStyle
        )
      }

      fun drawBottomLabel() {
        val rpmTextMeasure = measureText("RPM")
        drawText(
          rpmTextMeasure,
          topLeft = Offset(
            centerCoordinate.x - (rpmTextMeasure.size.width / 2),
            layoutHeight - bottomLabelMeasure
          ),
          color = textColor
        )
      }

      fun drawYLabels() {
        if (sorted.isEmpty()) {
          return
        }
        val minMax = findMinMax(sorted)
        val step = (minMax.second - minMax.first) / 4
        for (i in 0..5) {
          val value = minMax.first + (step * i)
          val label = value.toInt().toString()
          val labelMeasure = measureText(label)
          val centerMeasure = labelMeasure.size.height / 2
          drawText(
            labelMeasure,
            topLeft = Offset(
              0f - (labelMeasure.size.width / 2),
              findYPosition(value, minMax)
                  - centerMeasure
            ),
            color = textColor
          )
        }
      }

      fun drawXLabels() {
        if (sorted.isEmpty()) {
          return
        }
        val minRpm = sorted.first().rpm
        val maxRpm = sorted.last().rpm
        val step = (maxRpm - minRpm) / 4

        for (i in 0..5) {
          val value = minRpm + (step * i)
          val label = roundRpmToStep(value).toString()
          val labelMeasure = measureText(label)
          val xStep = layoutWidth / 4
          val xPos = if(i == 0) 0f else (xStep * i) - (labelMeasure.size.width / 2)
          drawText(
            labelMeasure,
            topLeft = Offset(
              xPos ,
              layoutHeight
                  - (bottomLabelMeasure + (labelMeasure.size.height))
            ),
            color = textColor
          )
        }
      }

      drawYLabels()
      drawXLabels()
      drawBottomLabel()
      drawPowerPaths()
    }
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(top = 8.dp)
        .align(Alignment.CenterHorizontally),
      horizontalArrangement = Arrangement.SpaceEvenly
    ) {
      Row(
        modifier = Modifier
          .padding(end = 12.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .height(12.dp)
            .width(12.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(hpColor)
        )
        Text(
          modifier = Modifier
            .padding(start = 18.dp),
          text = "Horsepower"
        )
      }
      Row(
        modifier = Modifier,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .height(12.dp)
            .width(12.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(tqColor)
        )
        Text(
          modifier = Modifier
            .padding(start = 18.dp),
          text = "Torque"
        )
      }
    }
  }
}