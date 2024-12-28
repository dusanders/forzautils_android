package com.example.forzautils.ui.dataViewer.hpTorque

import android.content.Context
import android.view.View
import com.example.forzautils.R
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

class ChartFactory(
    val context: Context
) {
    data class HpTqChart(
        val chart: LineChart,
        var id: Int
    )

    private val charts: ArrayList<HpTqChart> = ArrayList()

    fun clear() {
        charts.clear()
    }

    fun getChartForGear(gear: Int): HpTqChart {
        while (charts.count() < gear) {
            val lineChart = initializeLineChart(
                LineChart(context),
                String.format(
                    context.resources.getString(R.string.generic_gear),
                    charts.count() + 1
                )
            )
            lineChart.id = View.generateViewId()
            charts.add(HpTqChart(
                chart = lineChart,
                id = lineChart.id
            ))
        }
        return charts[gear - 1]
    }

    private fun initializeLineChart(chart: LineChart, desc: String): LineChart {
        val lineGraph = initializeChartDataSets(chart)
        val textColor = context.resources
            .getColor(R.color.hpTorque_text, context.theme)

        lineGraph.xAxis.textColor = textColor
        lineGraph.xAxis.setDrawGridLines(false)
        lineGraph.axisLeft.textColor =
            context.resources
                .getColor(R.color.hpTorque_torqueLine, context.theme)
        lineGraph.axisLeft.setDrawGridLines(false)

        lineGraph.axisRight.textColor = context.resources
            .getColor(R.color.hpTorque_hpLine, context.theme)
        lineGraph.axisRight.setDrawGridLines(false)
        lineGraph.legend.textColor = textColor

        val graphDesc = Description()
        graphDesc.text = desc
        graphDesc.textColor = textColor
        graphDesc.textSize = context.resources
            .getDimension(R.dimen.fontSize_large)
        lineGraph.description = graphDesc

        lineGraph.setTouchEnabled(false)
        lineGraph.setPinchZoom(false)
        return lineGraph
    }

    private fun initializeChartDataSets(lineGraph: LineChart): LineChart {
        val hpEntries = ArrayList<Entry>()
        val hpDataSet = LineDataSet(
            hpEntries,
            context.resources.getString(R.string.generic_horsepower)
        )
        hpDataSet.color = context.resources
            .getColor(R.color.hpTorque_hpLine, context.theme)
        hpDataSet.axisDependency = YAxis.AxisDependency.RIGHT
        hpDataSet.valueTextColor = context.resources
            .getColor(R.color.hpTorque_hpLine, context.theme)
        hpDataSet.lineWidth = 2f

        val tqEntries = ArrayList<Entry>()
        val tqDataSet = LineDataSet(
            tqEntries,
            context.resources
                .getString(R.string.generic_torque)
        )
        tqDataSet.color = context.resources
            .getColor(R.color.hpTorque_torqueLine, context.theme)
        tqDataSet.valueTextColor = context.resources
            .getColor(R.color.hpTorque_torqueLine, context.theme)
        tqDataSet.lineWidth = 2f

        lineGraph.data = LineData(hpDataSet, tqDataSet)
        return lineGraph
    }
}