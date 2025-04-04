package com.example.forzautils.ui.components

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import com.example.forzautils.ui.theme.FontSizes

@SuppressLint("DefaultLocale")
fun limitPrecision(value: Float): Float {
  return String.format("%.2f", value).toFloat()
}
fun convertValueToDegrees(value: Float, min: Float, max: Float): Float {
  if(value == 0f && min == 0f && max == 0f){
    return 0f
  }
  val divisor = max - min
  if(divisor <= 0){
    return 360f
  }
  if(value < min){
    return 0f
  }
  val degrees = (value - min) / divisor * 360
  return degrees
}
@Composable
fun RadialGauge(
  label: String,
  value: Float,
  min: Float = 0f,
  max: Float = 100f
) {
  val arcColor = MaterialTheme.colorScheme.primary
  val valcolor = MaterialTheme.colorScheme.tertiary
  val screenWidth = LocalConfiguration.current.screenWidthDp
  var width by remember {
    mutableStateOf((screenWidth * 0.30f).dp)
  }
  LaunchedEffect(screenWidth) {
    width = (screenWidth * 0.30f).dp
  }
  val textMeasurer = rememberTextMeasurer()
  val textStyle = TextStyle(
    color = MaterialTheme.colorScheme.primary,
    fontSize = FontSizes.lg
  )
  val normalizedValue = limitPrecision(value)
  val normalizedMin = limitPrecision(min)
  val normalizedMax = limitPrecision(max)
  val valueText = normalizedValue.toString()
  val textRenderResult = remember(valueText) {
    textMeasurer.measure(
      text = valueText,
      style = textStyle
    )
  }
  Column {
    Text(
      modifier = Modifier
        .align(Alignment.CenterHorizontally),
      text = label
    )
    Canvas(
      modifier = Modifier
        .requiredSize(width)
        .padding(12.dp)
    ) {
      drawArc(
        color = arcColor,
        startAngle = 90f,
        sweepAngle = 360f,
        useCenter = false,
        style = Stroke(width = 25f)
      )
      drawArc(
        color = valcolor,
        startAngle = 90f,
        sweepAngle = convertValueToDegrees(
          normalizedValue,
          normalizedMin,
          normalizedMax
        ),
        useCenter = false,
        style = Stroke(width = 25f)
      )
      drawText(
        textRenderResult,
        color = arcColor,
        topLeft = Offset(
          (size.width - textRenderResult.size.width) / 2,
          (size.height - textRenderResult.size.height) / 2
        )
      )
    }
  }
}