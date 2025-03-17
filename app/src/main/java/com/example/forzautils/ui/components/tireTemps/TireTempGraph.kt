package com.example.forzautils.ui.components.tireTemps

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianLayerRangeProvider
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer

@Composable
fun TireTempGraph(
  dataPoints: List<Float>,
  label: String
) {
  val tag = "TireTempGraph($label)"
  val producer = remember { CartesianChartModelProducer() }

  LaunchedEffect(dataPoints) {
    if(dataPoints.isEmpty()) {
      return@LaunchedEffect;
    }
    producer.runTransaction {
      lineSeries {
        series(dataPoints)
      }
    }
  }
  Column(
    modifier = Modifier.fillMaxWidth()
  ) {
    Text(
      text = label
    )
    CartesianChartHost(
      modifier = Modifier.fillMaxWidth(),
      modelProducer = producer,
      chart = rememberCartesianChart(
        rememberLineCartesianLayer(
          lineProvider = LineCartesianLayer.LineProvider.series(
            lines = listOf(
              LineCartesianLayer.rememberLine(
                fill = LineCartesianLayer.LineFill
                  .single(fill(MaterialTheme.colorScheme.tertiary))
              )
            )
          ),
          rangeProvider = CartesianLayerRangeProvider.auto()
        ),
        startAxis = VerticalAxis.rememberStart(
          guideline = null
        )
      ),
    )
  }
}