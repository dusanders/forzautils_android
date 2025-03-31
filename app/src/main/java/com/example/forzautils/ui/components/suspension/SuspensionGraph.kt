package com.example.forzautils.ui.components.suspension

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.unit.dp
import com.example.forzautils.utils.CanvasCoordinate

@Composable
fun SuspensionGraph(
  totalSize: Int,
  events: List<Float>,
  diff: List<Float>? = null
) {
  val tag = "SuspensionGraph"
  val scalar = 1
  var xNormalizer by remember { mutableFloatStateOf(0f) }
  var layoutHeight by remember { mutableFloatStateOf(0f) }
  var layoutWidth by remember { mutableFloatStateOf(0f) }
  var centerCoordinate by remember { mutableStateOf(CanvasCoordinate(0f, 0f)) }

  LaunchedEffect(layoutWidth, totalSize) {
    val xMovements = (layoutWidth / totalSize)
    Log.d(tag, "xMovements: $layoutWidth / ${totalSize} = $xMovements")
    xNormalizer = xMovements
  }

  val yBaseline = centerCoordinate.y
  val eventsPath = Path()
  val eventsColor = MaterialTheme.colorScheme.primary
  val diffPath = Path()
  val diffColor = MaterialTheme.colorScheme.secondary

  diff?.forEachIndexed { index, it ->
    if (index == 0) {
      diffPath.moveTo(0f, yBaseline)
    } else {
      val xMove = index.toFloat() * xNormalizer
      diffPath.lineTo(
        xMove,
        yBaseline - (it * scalar)
      )
    }
  }

  events.forEachIndexed { index, it ->
    if (index == 0) {
      eventsPath.moveTo(0f, yBaseline)
    } else {
      val xMove = index.toFloat() * xNormalizer
      eventsPath.lineTo(
        xMove,
        yBaseline - (it * scalar)
      )
    }
  }

  Canvas(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(12.dp))
      .height(75.dp)
      .background(MaterialTheme.colorScheme.surface)
      .onPlaced { coords ->
        layoutHeight = coords.size.height.toFloat()
        layoutWidth = coords.size.width.toFloat()
        centerCoordinate = CanvasCoordinate(
          x = layoutWidth / 2,
          y = layoutHeight / 2
        )
      }
  ) {
    if(diffPath.isEmpty.not()) {
      drawPath(
        diffPath,
        color = diffColor,
        style = Stroke(
          width = 1.5.dp.toPx(),
          pathEffect = PathEffect.cornerPathEffect(8.dp.toPx()),
          cap = Stroke.DefaultCap
        )
      )
    }
    drawPath(
      eventsPath,
      color = eventsColor,
      style = Stroke(
        width = 1.5.dp.toPx(),
        pathEffect = PathEffect.cornerPathEffect(8.dp.toPx()),
        cap = Stroke.DefaultCap
      )
    )
  }
}