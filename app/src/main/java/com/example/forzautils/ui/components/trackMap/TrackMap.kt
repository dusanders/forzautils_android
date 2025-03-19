package com.example.forzautils.ui.components.trackMap

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.unit.dp
import com.example.forzautils.viewModels.interfaces.IForzaDataStream

@Composable
fun TrackMap(
  forzaViewModel: IForzaDataStream
) {
  val tag = "TrackMap"
  val pathColor = MaterialTheme.colorScheme.primary
  val canvasCenterX = remember { mutableFloatStateOf(0f) }
  val canvasCenterY = remember { mutableFloatStateOf(0f) }

  MapDrawing(
    forzaViewModel,
    centerX = canvasCenterX.floatValue,
    centerY = canvasCenterY.floatValue
  ) { path, current ->
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .padding(start = 24.dp, end = 24.dp, bottom = 24.dp)
        .height(150.dp)
        .clip(RoundedCornerShape(12.dp))
        .background(MaterialTheme.colorScheme.surface)
    ) {
      Canvas(
        modifier = Modifier
          .fillMaxSize()
          .onPlaced { coords ->
            val height = coords.size.height
            val width = coords.size.width
            canvasCenterX.floatValue = (width / 2).toFloat()
            canvasCenterY.floatValue = (height / 2).toFloat()
          },
      ) {
        drawPath(
          path,
          color = pathColor,
          style = Stroke(
            width = 3.dp.toPx(),
            pathEffect = PathEffect.cornerPathEffect(8.dp.toPx()),
            cap = Stroke.DefaultCap
          )
        )
        drawCircle(
          color = pathColor,
          radius = 10.dp.toPx(),
          center = Offset(current.positionX, current.positionY)
        )
      }
    }
  }
}