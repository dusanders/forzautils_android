package com.example.forzautils.ui.components.engineInfo

import android.text.Layout
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.forzautils.R
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.common.component.shapeComponent
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.compose.common.rememberHorizontalLegend
import com.patrykandpatrick.vico.compose.common.rememberVerticalLegend
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.core.common.Insets
import com.patrykandpatrick.vico.core.common.LegendItem
import com.patrykandpatrick.vico.core.common.data.ExtraStore
import com.patrykandpatrick.vico.core.common.shape.CorneredShape
import forza.telemetry.data.models.EngineModel

val LegendColorMap = ExtraStore.Key<Set<String>>()

@Composable
fun EngineInfo(dataModel: EngineModel? = null) {
  val producer = remember { CartesianChartModelProducer() }
  val gearLabel = LocalContext.current.getString(R.string.generic_gear, dataModel?.gear)
  val horsepowerLabel = LocalContext.current.getString(R.string.generic_horsepower)
  val torqueLabel = LocalContext.current.getString(R.string.generic_torque)
  val colorMap = mapOf(
    horsepowerLabel
        to Color(
      LocalContext.current.getColor(
        R.color.hpTorque_hpLine
      )
    ),
    torqueLabel
        to Color(
      LocalContext.current.getColor(
        R.color.hpTorque_torqueLine
      )
    )
  )
  val labelComponent =
    rememberTextComponent(
      color = MaterialTheme.colorScheme.primary,
      textAlignment = Layout.Alignment.ALIGN_CENTER
    )
  LaunchedEffect(Unit) {
    producer.runTransaction {
      lineSeries {
        series(
          listOf(1200f, 1250f, 1300f, 1350f, 1400f, 1450f, 1500f, 1550f, 1600f, 1650f, 1700f),
          listOf(0f, 1f, 2f, 3f, 4f, 5f, 6f, 7f, 8f, 9f, 10f),
        )
        series(
          listOf(1200f, 1250f, 1300f, 1350f, 1400f, 1450f, 1500f, 1550f, 1600f, 1650f, 1700f),
          listOf(100f, 200f, 300f, 400f, 500f, 600f, 700f, 800f, 900f, 1000f, 1100f)
        )
        extras { store ->
          store[LegendColorMap] = colorMap.keys
        }
      }
    }
  }
  Column(
    modifier = Modifier
      .padding(12.dp)
      .clip(RoundedCornerShape(12.dp))
      .background(MaterialTheme.colorScheme.surface)
  ) {
    Text(
      text = gearLabel,
      color = MaterialTheme.colorScheme.primary,
      modifier = Modifier
        .padding(bottom = 8.dp)
        .align(Alignment.CenterHorizontally),
    )
    CartesianChartHost(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp),
      chart = rememberCartesianChart(
        rememberLineCartesianLayer(
          LineCartesianLayer.LineProvider.series(
            lines = listOf(
              LineCartesianLayer.rememberLine(
                fill = LineCartesianLayer.LineFill
                  .single(fill(colorMap[horsepowerLabel]!!))
              ),
              LineCartesianLayer.rememberLine(
                fill = LineCartesianLayer.LineFill
                  .single(fill(colorMap[torqueLabel]!!))
              )
            )
          )
        ),
        startAxis = VerticalAxis.rememberStart(
          guideline = null,
        ),
        bottomAxis = HorizontalAxis.rememberBottom(
          guideline = null,
          titleComponent = rememberTextComponent(
            color = MaterialTheme.colorScheme.primary
          ),
          title = "RPM"
        ),
        legend = rememberHorizontalLegend(
          items = { store ->
            store[LegendColorMap].forEachIndexed { index, item ->
              add(
                LegendItem(
                  icon = shapeComponent(fill(colorMap.get(item)!!), CorneredShape.Pill),
                  labelComponent = labelComponent,
                  label = item,
                )
              )
            }
          },
          padding = Insets(startDp = 36f),
        ),
      ),
      modelProducer = producer,
    )
  }
}