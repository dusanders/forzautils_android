package com.example.forzautils.ui.dataViewer.hpTorque

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.OnClickListener
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.example.forzautils.R
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.android.material.button.MaterialButton

class HpTorqueFragment : Fragment() {
    private val _tag = "HpTorqueFragment"
    private var isShowingCurves: Boolean = false
    private val viewModel: HpTorqueViewModel by activityViewModels()
    private lateinit var view: View
    private var charts: ArrayList<LineChart> = ArrayList()

    private val forzaObserver = Observer<HpTorqueViewModel.DataUpdate> { updated ->
        runOnUiThread {
            handleIncomingData(updated)
        }
    }

    private val backButtonListener = OnClickListener { v ->
        viewModel.onBackClick()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        view = inflater.inflate(R.layout.fragment_hp_torque, container, false)
        showWaitingForData()
        return view
    }

    override fun onResume() {
        super.onResume()
        attachObservers()

        if (charts.isEmpty()) {
            showWaitingForData()
        } else {
            showCurves()
        }
        // TODO - Remove debug values
//        Handler(Looper.myLooper()!!).postDelayed({
//            viewModel.ADD_DEBUG()
//        }, 3000)

    }

    override fun onStop() {
        super.onStop()
        removeObservers()
    }

    private fun handleIncomingData(updated: HpTorqueViewModel.DataUpdate) {
        val rpmMap = viewModel.dataMap[updated.gear]
        if (rpmMap == null) {
            Log.w(_tag, "ViewModel missing data for ${updated.gear} @ ${updated.rpm}")
            return
        }
        val rpmKeys = rpmMap.keys.sorted()
        fun hpValues(): ArrayList<Entry> {
            val hpEntries = ArrayList<Entry>()
            rpmKeys.forEach { key ->
                val entry = rpmMap[key]
                hpEntries.add(Entry(key.toFloat(), entry!!.hp))
            }
            return hpEntries
        }

        fun tqValues(): ArrayList<Entry> {
            val tqEntries = ArrayList<Entry>()
            rpmKeys.forEach { key ->
                val entry = rpmMap[key]
                tqEntries.add(Entry(key.toFloat(), entry!!.tq))
            }
            return tqEntries
        }

        val chart = getChartForGear(updated.gear)
        Log.d(_tag, "Chart for ${updated.gear}")
        val hpData = chart.data.dataSets[0] as LineDataSet
        val tqData = chart.data.dataSets[1] as LineDataSet
        if (!isShowingCurves) {
            showCurves()
        }

        hpData.values = hpValues()
        tqData.values = tqValues()

        chart.invalidate()
        chart.data.notifyDataChanged()
        chart.notifyDataSetChanged()
    }

    private fun runOnUiThread(runnable: Runnable) {
        activity?.runOnUiThread(runnable)
    }

    private fun showWaitingForData() {
        isShowingCurves = false
        view.findViewById<LinearLayout>(R.id.hpTorque_waitForDataLayout)
            .visibility = VISIBLE
        view.findViewById<ScrollView>(R.id.hpTorque_scrollView)
            .visibility = GONE
    }

    private fun showCurves() {
        isShowingCurves = true
        view.findViewById<LinearLayout>(R.id.hpTorque_waitForDataLayout)
            .visibility = GONE
        view.findViewById<ScrollView>(R.id.hpTorque_scrollView)
            .visibility = VISIBLE
    }

    private fun getChartForGear(gear: Int): LineChart {
        fun addLineGraphToView(lineGraph: LineChart) {
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                resources.getDimension(R.dimen.hpTorque_lineChartHeight).toInt()
            )
            params.setMargins(0, 20, 0, 20)
            view.findViewById<LinearLayout>(R.id.hpTorque_curvesRoot)
                .addView(lineGraph, params)
        }

        var chart: LineChart
        while (charts.count() < gear) {
            chart = initializeLineChart(
                LineChart(context),
                String.format(
                    resources.getString(R.string.generic_gear),
                    charts.count() + 1
                )
            )
            charts.add(chart)
            addLineGraphToView(chart)
        }
        chart = charts[gear - 1]
        return chart
    }

    private fun initializeLineChart(chart: LineChart, desc: String): LineChart {
        val lineGraph = initializeChartDataSets(chart)
        val textColor = resources.getColor(R.color.hpTorque_text, context?.theme)

        lineGraph.xAxis.textColor = textColor
        lineGraph.xAxis.setDrawGridLines(false)
        lineGraph.axisLeft.textColor =
            resources.getColor(R.color.hpTorque_torqueLine, context?.theme)
        lineGraph.axisLeft.setDrawGridLines(false)

        lineGraph.axisRight.textColor = resources.getColor(R.color.hpTorque_hpLine, context?.theme)
        lineGraph.axisRight.setDrawGridLines(false)
        lineGraph.legend.textColor = textColor

        val graphDesc = Description()
        graphDesc.text = desc
        graphDesc.textColor = textColor
        graphDesc.textSize = resources.getDimension(R.dimen.fontSize_large)
        lineGraph.description = graphDesc

        lineGraph.setTouchEnabled(false)
        lineGraph.setPinchZoom(false)
        return lineGraph
    }

    private fun initializeChartDataSets(lineGraph: LineChart): LineChart {
        val hpEntries = ArrayList<Entry>()
        val hpDataSet = LineDataSet(hpEntries, resources.getString(R.string.generic_horsepower))
        hpDataSet.color = resources.getColor(R.color.hpTorque_hpLine, context?.theme)
        hpDataSet.axisDependency = YAxis.AxisDependency.RIGHT
        hpDataSet.valueTextColor = resources.getColor(R.color.hpTorque_hpLine, context?.theme)
        hpDataSet.lineWidth = 2f

        val tqEntries = ArrayList<Entry>()
        val tqDataSet = LineDataSet(tqEntries, resources.getString(R.string.generic_torque))
        tqDataSet.color = resources.getColor(R.color.hpTorque_torqueLine, context?.theme)
        tqDataSet.valueTextColor = resources.getColor(R.color.hpTorque_torqueLine, context?.theme)
        tqDataSet.lineWidth = 2f

        lineGraph.data = LineData(hpDataSet, tqDataSet)
        return lineGraph
    }

    private fun attachObservers() {
        viewModel.dataUpdated.observe(this, forzaObserver)
        view.findViewById<MaterialButton>(R.id.hpTorque_backButton)
            .setOnClickListener(backButtonListener)
    }

    private fun removeObservers() {
        viewModel.dataUpdated.removeObserver(forzaObserver)
    }
}